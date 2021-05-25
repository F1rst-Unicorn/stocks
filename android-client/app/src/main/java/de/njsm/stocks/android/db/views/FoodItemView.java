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

package de.njsm.stocks.android.db.views;

import androidx.annotation.NonNull;

import org.threeten.bp.Instant;

import de.njsm.stocks.android.db.entities.VersionedData;

import java.math.BigDecimal;

public class FoodItemView extends VersionedData {

    private final String userName;

    private final String deviceName;

    private final Instant eatByDate;

    private final String location;

    private final int ofType;

    private final int storedIn;

    private final int scaledUnit;

    private final BigDecimal scale;

    private final String unitAbbreviation;

    public FoodItemView(int id, @NonNull Instant validTimeStart, @NonNull Instant validTimeEnd, @NonNull Instant transactionTimeStart, @NonNull Instant transactionTimeEnd, int version, int initiates, String userName, String deviceName, Instant eatByDate, String location, int ofType, int storedIn, int scaledUnit, BigDecimal scale, String unitAbbreviation) {
        super(id, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, version, initiates);
        this.userName = userName;
        this.deviceName = deviceName;
        this.eatByDate = eatByDate;
        this.location = location;
        this.ofType = ofType;
        this.storedIn = storedIn;
        this.scaledUnit = scaledUnit;
        this.scale = scale;
        this.unitAbbreviation = unitAbbreviation;
    }

    public String getUserName() {
        return userName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public Instant getEatByDate() {
        return eatByDate;
    }

    public String getLocation() {
        return location;
    }

    public int getOfType() {
        return ofType;
    }

    public int getStoredIn() {
        return storedIn;
    }

    public int getScaledUnit() {
        return scaledUnit;
    }

    public BigDecimal getScale() {
        return scale;
    }

    public String getUnitAbbreviation() {
        return unitAbbreviation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FoodItemView that = (FoodItemView) o;

        if (!userName.equals(that.userName)) return false;
        if (!deviceName.equals(that.deviceName)) return false;
        if (!eatByDate.equals(that.eatByDate)) return false;
        return location.equals(that.location);
    }

    @Override
    public int hashCode() {
        int result = userName.hashCode();
        result = 31 * result + deviceName.hashCode();
        result = 31 * result + eatByDate.hashCode();
        result = 31 * result + location.hashCode();
        return result;
    }
}
