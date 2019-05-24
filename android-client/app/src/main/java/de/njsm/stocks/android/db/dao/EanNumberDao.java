package de.njsm.stocks.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import de.njsm.stocks.android.db.entities.EanNumber;

import java.util.List;

@Dao
public abstract class EanNumberDao {

    @Query("SELECT * FROM EanNumber " +
            "WHERE identifies = :foodId")
    public abstract LiveData<List<EanNumber>> getEanNumbersOf(int foodId);

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
