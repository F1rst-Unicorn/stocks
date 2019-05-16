package de.njsm.stocks.android.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import de.njsm.stocks.android.db.entities.FoodItem;

@Dao
public abstract class FoodItemDao {

    @Transaction
    public void synchronise(FoodItem[] food) {
        delete();
        insert(food);
    }

    @Insert
    abstract void insert(FoodItem[] food);

    @Query("DELETE FROM FoodItem")
    abstract void delete();
}
