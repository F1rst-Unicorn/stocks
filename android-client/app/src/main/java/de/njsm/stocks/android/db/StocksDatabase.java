package de.njsm.stocks.android.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import de.njsm.stocks.android.db.dao.UpdateDao;
import de.njsm.stocks.android.db.dao.UserDao;
import de.njsm.stocks.android.db.entities.Update;
import de.njsm.stocks.android.db.entities.User;

@Database(entities = {
        User.class,
        Update.class,
}, version = 21)
@TypeConverters(de.njsm.stocks.android.db.TypeConverters.class)
public abstract class StocksDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract UpdateDao updateDao();
}
