package de.njsm.stocks.android.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import de.njsm.stocks.android.db.dao.UpdateDao;
import de.njsm.stocks.android.db.dao.UserDao;
import de.njsm.stocks.android.db.dao.UserDeviceDao;
import de.njsm.stocks.android.db.entities.Update;
import de.njsm.stocks.android.db.entities.User;
import de.njsm.stocks.android.db.entities.UserDevice;

@Database(entities = {
        User.class,
        UserDevice.class,
        Update.class,
}, version = 22)
@TypeConverters(de.njsm.stocks.android.db.TypeConverters.class)
public abstract class StocksDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract UserDeviceDao userDeviceDao();

    public abstract UpdateDao updateDao();
}
