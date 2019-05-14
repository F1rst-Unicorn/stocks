package de.njsm.stocks.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import de.njsm.stocks.android.db.entities.UserDevice;

import java.util.List;

@Dao
public abstract class UserDeviceDao {

    @Query("SELECT * FROM User_device WHERE belongs_to = :userId")
    public abstract LiveData<List<UserDevice>> getDevicesOfUser(int userId);

    @Transaction
    public void synchronise(UserDevice[] users) {
        delete();
        insert(users);
    }

    @Insert
    abstract void insert(UserDevice[] users);

    @Query("DELETE FROM User_device")
    abstract void delete();
}
