package de.njsm.stocks.android.dagger.modules;

import android.app.Application;
import androidx.room.Room;
import dagger.Module;
import dagger.Provides;
import de.njsm.stocks.android.db.StocksDatabase;
import de.njsm.stocks.android.db.dao.LocationDao;
import de.njsm.stocks.android.db.dao.UpdateDao;
import de.njsm.stocks.android.db.dao.UserDao;
import de.njsm.stocks.android.db.dao.UserDeviceDao;

import javax.inject.Singleton;

@Module
public abstract class DbModule {

    @Provides
    @Singleton
    static StocksDatabase provideDatabase(Application context) {
        return Room.databaseBuilder(context, StocksDatabase.class, "stocks.db")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    static UserDao provideUserDao(StocksDatabase database) {
        return database.userDao();
    }

    @Provides
    static LocationDao provideLocationDao(StocksDatabase database) {
        return database.locationDao();
    }

    @Provides
    static UserDeviceDao provideUserDeviceDao(StocksDatabase database) {
        return database.userDeviceDao();
    }

    @Provides
    static UpdateDao provideUpdateDao(StocksDatabase database) {
        return database.updateDao();
    }
}
