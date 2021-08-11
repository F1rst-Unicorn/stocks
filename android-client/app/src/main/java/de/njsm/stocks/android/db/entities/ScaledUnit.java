/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.stocks.android.db.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.threeten.bp.Instant;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Objects;

import static java.math.BigDecimal.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
@Entity(tableName = "scaled_unit",
        primaryKeys = {"_id", "version", "transaction_time_start"},
        indices = {
                @Index(value = {"_id", "valid_time_start", "valid_time_end"}, name = "scaled_unit_current"),
                @Index(value = {"_id"}, name = "scaled_unit_pkey"),
                @Index(value = {"transaction_time_start"}, name = "scaled_unit_transaction_time_start"),
                @Index(value = {"transaction_time_end"}, name = "scaled_unit_transaction_time_end"),
        })
public class ScaledUnit extends VersionedData {

    private static final BigDecimal THOUSAND = new BigDecimal(1000);

    private static final String[] PREFIXS = new String[] {
            "Y",
            "Z",
            "E",
            "P",
            "T",
            "G",
            "M",
            "k",
            "",
            "m",
            "μ",
            "n",
            "p",
            "f",
            "a",
            "z",
            "y",
    };

    @ColumnInfo(name = "scale")
    @NonNull
    public BigDecimal scale;

    @ColumnInfo(name = "unit")
    @NonNull
    public int unit;

    public ScaledUnit(int id, @NonNull Instant validTimeStart, @NonNull Instant validTimeEnd, @NonNull Instant transactionTimeStart, @NonNull Instant transactionTimeEnd, int version, int initiates, @NonNull BigDecimal scale, int unit) {
        super(id, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, version, initiates);
        this.scale = scale;
        this.unit = unit;
    }

    @Ignore
    public ScaledUnit() {}

    public String normalise() {
        int exponentPivot = getExponentPivot(scale);
        return toLocaleString(exponentPivot, scale) + getScalePrefix(exponentPivot);
    }

    private static String toLocaleString(int exponentPivot, BigDecimal scale) {
        BigDecimal number = shrinkNumber(scale, exponentPivot);
        StringBuffer buffer = new StringBuffer();
        return NumberFormat.getInstance()
                .format(number, buffer, new FieldPosition(0)).toString();
    }

    public static String normalise(BigDecimal scale) {
        int exponentPivot = getExponentPivot(scale);
        return toLocaleString(exponentPivot, scale) + getScalePrefix(exponentPivot);
    }

    public static BigDecimal shrinkNumber(BigDecimal input) {
        return shrinkNumber(input, getExponentPivot(input));
    }

    private static BigDecimal shrinkNumber(BigDecimal input, int exponentPivot) {
        BigDecimal factor;
        if (exponentPivot > 0) {
            factor = ONE.divide(TEN, MathContext.UNLIMITED);
        } else {
            exponentPivot *= -1;
            factor = TEN;
        }
        return input.multiply(factor.pow(3 * exponentPivot)).stripTrailingZeros();
    }

    private static int getExponentPivot(BigDecimal input) {
        int result = 0;
        if (input.compareTo(ZERO) == 0)
            return result;

        BigDecimal iterator = input;
        while (iterator.compareTo(THOUSAND) >= 0) {
            result++;
            iterator = iterator.divide(THOUSAND, MathContext.UNLIMITED);
        }

        while (iterator.compareTo(BigDecimal.ONE) < 0) {
            result--;
            iterator = iterator.multiply(THOUSAND);
        }

        return result;
    }

    private static String getScalePrefix(int exponentPivot) {
        int index = 8 - exponentPivot;
        if (0 <= index && index < PREFIXS.length) {
            return PREFIXS[index];
        } else {
            return "?";
        }
    }

    public static String getScalePrefix(BigDecimal input) {
        return getScalePrefix(getExponentPivot(input));
    }

    @NonNull
    public BigDecimal getScale() {
        return scale;
    }

    public void setScale(@NonNull BigDecimal scale) {
        this.scale = scale;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScaledUnit)) return false;
        ScaledUnit that = (ScaledUnit) o;
        return getUnit() == that.getUnit() && getScale().compareTo(that.getScale()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getScale(), getUnit());
    }

    public ScaledUnit copy() {
        return new ScaledUnit(id, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, version, initiates, scale, unit);
    }
}
