package de.njsm.stocks.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import de.njsm.stocks.android.db.entities.Food;

import java.util.List;

@Dao
public abstract class FoodDao {

    @Query("SELECT * FROM Food")
    public abstract LiveData<List<Food>> getAll();

    @Transaction
    public void synchronise(Food[] food) {
        delete();
        insert(food);
    }

    @Insert
    abstract void insert(Food[] food);

    @Query("DELETE FROM Food")
    abstract void delete();

    @Query("SELECT * FROM Food WHERE _id NOT IN " +
            "(SELECT DISTINCT of_type FROM FoodItem)")
    public abstract LiveData<List<Food>> getEmptyFood();

    @Query("SELECT * FROM Food WHERE _id = :id")
    public abstract LiveData<Food> getFood(int id);
}
