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
import androidx.room.Index;
import com.google.common.base.Objects;

import java.time.Instant;

@Entity(tableName = "unit", primaryKeys = {"_id", "version", "transaction_time_start"},
        indices = {
                @Index(value = {"_id", "valid_time_start", "valid_time_end"}, name = "unit_current"),
                @Index(value = {"_id"}, name = "unit_pkey"),
                @Index(value = {"transaction_time_start"}, name = "unit_transaction_time_start"),
                @Index(value = {"transaction_time_end"}, name = "unit_transaction_time_end"),
        })
public class Unit extends VersionedData {

    @ColumnInfo(name = "name")
    @NonNull
    public String name;

    @ColumnInfo(name = "abbreviation")
    @NonNull
    public String abbreviation;

    public Unit(int id, @NonNull Instant validTimeStart, @NonNull Instant validTimeEnd, @NonNull Instant transactionTimeStart, @NonNull Instant transactionTimeEnd, int version, int initiates, String name, String abbreviation) {
        super(id, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, version, initiates);
        this.name = name;
        this.abbreviation = abbreviation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Unit unit = (Unit) o;
        return Objects.equal(getName(), unit.getName()) && Objects.equal(getAbbreviation(), unit.getAbbreviation());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), getName(), getAbbreviation());
    }
}
