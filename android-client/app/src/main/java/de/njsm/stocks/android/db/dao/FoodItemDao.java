package de.njsm.stocks.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import de.njsm.stocks.android.db.entities.FoodItem;
import de.njsm.stocks.android.db.views.FoodItemView;
import org.threeten.bp.Instant;

import java.util.List;

@Dao
public abstract class FoodItemDao {

    @Query("SELECT i._id as _id, i.version as version, u.name as userName, " +
            "d.name as deviceName, l.name as location, i.eat_by as eatByDate, " +
            "i.of_type as ofType, i.stored_in as storedIn " +
            "FROM FoodItem i " +
            "INNER JOIN User u ON i.buys = u._id " +
            "INNER JOIN User_device d ON i.registers = d._id " +
            "INNER JOIN Location l ON i.stored_in = l._id " +
            "WHERE i.of_type = :foodId " +
            "ORDER BY i.eat_by")
    public abstract LiveData<List<FoodItemView>> getItemsOfType(int foodId);

    @Query("SELECT i._id as _id, i.version as version, u.name as userName, " +
            "d.name as deviceName, l.name as location, i.eat_by as eatByDate, " +
            "i.of_type as ofType, i.stored_in as storedIn " +
            "FROM FoodItem i " +
            "INNER JOIN User u ON i.buys = u._id " +
            "INNER JOIN User_device d ON i.registers = d._id " +
            "INNER JOIN Location l ON i.stored_in = l._id " +
            "WHERE i._id = :id")
    public abstract LiveData<FoodItemView> getItem(int id);

    @Query("SELECT eat_by " +
            "FROM FoodItem " +
            "WHERE of_type = :foodType " +
            "ORDER BY eat_by DESC " +
            "LIMIT 1")
    public abstract LiveData<Instant> getLatestExpirationOf(int foodType);

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
