package de.njsm.stocks.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.views.FoodView;

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

    @Query("WITH least_item AS " +
                "(SELECT i.of_type, count(*) as amount, i.eat_by as eatBy " +
                "FROM FoodItem i GROUP BY i.of_type HAVING i.eat_by = MIN(i.eat_by)) " +
            "SELECT f._id, f.version, f.name as name, i.eatBy as eatBy, i.amount as amount FROM Food f " +
            "INNER JOIN least_item i ON i.of_type = f._id " +
            "ORDER BY eatBy")
    public abstract LiveData<List<FoodView>> getFoodToEat();


    @Query("WITH least_item AS " +
            "(SELECT i.of_type, count(*) as amount, i.eat_by as eatBy " +
            "FROM FoodItem i WHERE i.stored_in = :location GROUP BY i.of_type HAVING i.eat_by = MIN(i.eat_by)) " +
            "SELECT f._id, f.version, f.name as name, i.eatBy as eatBy, i.amount as amount FROM Food f " +
            "INNER JOIN least_item i ON i.of_type = f._id " +
            "ORDER BY eatBy")
    public abstract LiveData<List<FoodView>> getFoodByLocation(int location);

    @Query("SELECT * FROM Food f " +
            "INNER JOIN EanNumber n on n.identifies = f._id " +
            "WHERE n.number = :s " +
            "LIMIT 1")
    public abstract LiveData<Food> getFoodByEanNumber(String s);
}
