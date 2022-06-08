/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.client.database.error;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import com.google.auto.value.AutoValue;
import de.njsm.stocks.client.business.entities.ScaledUnitFields;
import de.njsm.stocks.client.database.IdFields;
import de.njsm.stocks.client.database.TransactionTimeFields;
import de.njsm.stocks.client.database.VersionFields;

import java.math.BigDecimal;
import java.time.Instant;

@AutoValue
@Entity(tableName = "scaled_unit_to_edit")
public abstract class ScaledUnitEditEntity implements IdFields, VersionFields, TransactionTimeFields, ScaledUnitFields {

    @ColumnInfo(name = "scaled_unit_id")
    @AutoValue.CopyAnnotations
    public abstract int scaledUnitId();

    @ColumnInfo(name = "execution_time")
    @AutoValue.CopyAnnotations
    public abstract Instant executionTime();

    public static ScaledUnitEditEntity create(int id, int version, Instant transactionTime, Instant executionTime, BigDecimal scale, int unit, int scaledUnitId) {
        return new AutoValue_ScaledUnitEditEntity(id, version, transactionTime, scale, unit, scaledUnitId, executionTime);
    }

    @Ignore
    public static ScaledUnitEditEntity create(int scaledUnitId, int version, Instant transactionTime, Instant executionTime, BigDecimal scale, int unit) {
        return new AutoValue_ScaledUnitEditEntity(0, version, transactionTime, scale, unit, scaledUnitId, executionTime);
    }
}
