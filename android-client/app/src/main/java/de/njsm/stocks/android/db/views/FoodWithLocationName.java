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
import androidx.room.ColumnInfo;

import org.threeten.bp.Instant;

import de.njsm.stocks.android.db.entities.Food;

public class FoodWithLocationName extends Food {

    @ColumnInfo(name = "location_name")
    public String locationName;

    public FoodWithLocationName(int id, @NonNull Instant validTimeStart, @NonNull Instant validTimeEnd, @NonNull Instant transactionTimeStart, @NonNull Instant transactionTimeEnd, int version, int initiates, String name, boolean toBuy, int expirationOffset, int location, String locationName) {
        super(0, id, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, version, initiates, name, toBuy, expirationOffset, location);
        this.locationName = locationName;
    }
}
