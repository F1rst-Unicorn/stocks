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
}, version = 24)
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
