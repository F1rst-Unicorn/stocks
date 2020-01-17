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

package de.njsm.stocks.android.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import de.njsm.stocks.android.db.dao.*;
import de.njsm.stocks.android.db.entities.*;

@Database(entities = {
        User.class,
        UserDevice.class,
        Update.class,
        Location.class,
        Food.class,
        FoodItem.class,
        EanNumber.class,
}, version = 27)
@TypeConverters(de.njsm.stocks.android.db.TypeConverters.class)
public abstract class StocksDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract UserDeviceDao userDeviceDao();

    public abstract LocationDao locationDao();

    public abstract UpdateDao updateDao();

    public abstract FoodDao foodDao();

    public abstract FoodItemDao foodItemDao();

    public abstract EanNumberDao eanNumberDao();
}
