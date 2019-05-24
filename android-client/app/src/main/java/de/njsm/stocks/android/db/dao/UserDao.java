package de.njsm.stocks.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import de.njsm.stocks.android.db.entities.User;

import java.util.List;

@Dao
public abstract class UserDao {

    @Query("SELECT * FROM User")
    public abstract LiveData<List<User>> getAll();

    @Query("SELECT * FROM User WHERE _id = :userId")
    public abstract LiveData<User> getUser(int userId);

    @Transaction
    public void synchronise(User[] users) {
        delete();
        insert(users);
    }

    @Insert
    abstract void insert(User[] users);

    @Query("DELETE FROM User")
    abstract void delete();
}
