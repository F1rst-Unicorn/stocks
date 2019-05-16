package de.njsm.stocks.android.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import de.njsm.stocks.android.db.entities.EanNumber;

@Dao
public abstract class EanNumberDao {

    @Transaction
    public void synchronise(EanNumber[] food) {
        delete();
        insert(food);
    }

    @Insert
    abstract void insert(EanNumber[] food);

    @Query("DELETE FROM EanNumber")
    abstract void delete();
}
