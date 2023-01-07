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

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import com.google.auto.value.AutoValue;
import de.njsm.stocks.client.database.IdFields;
import de.njsm.stocks.client.database.PreservedId;

@AutoValue
@Entity(tableName = "ean_number_to_add")
public abstract class EanNumberAddEntity implements IdFields {

    @ColumnInfo(name = "ean_number")
    @NonNull
    @AutoValue.CopyAnnotations
    public abstract String eanNumber();

    @Embedded(prefix = "identifies_")
    @NonNull
    @AutoValue.CopyAnnotations
    public abstract PreservedId identifies();

    public static EanNumberAddEntity create(int id, String eanNumber, PreservedId identifies) {
        return new AutoValue_EanNumberAddEntity(id, eanNumber, identifies);
    }

    @Ignore
    public static EanNumberAddEntity create(String eanNumber, PreservedId identifies) {
        return new AutoValue_EanNumberAddEntity(0, eanNumber, identifies);
    }
}
