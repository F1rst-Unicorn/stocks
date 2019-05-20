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
                "(SELECT i.of_type, count(*) AS amount, i.eat_by AS eatBy " +
                "FROM FoodItem i GROUP BY i.of_type HAVING i.eat_by = MIN(i.eat_by)) " +
            "SELECT f._id, f.version, f.name AS name, i.eatBy AS eatBy, i.amount AS amount FROM Food f " +
            "INNER JOIN least_item i ON i.of_type = f._id " +
            "ORDER BY eatBy")
    public abstract LiveData<List<FoodView>> getFoodToEat();


    @Query("WITH least_item AS " +
            "(SELECT i.of_type, count(*) AS amount, i.eat_by AS eatBy " +
            "FROM FoodItem i WHERE i.stored_in = :location GROUP BY i.of_type HAVING i.eat_by = MIN(i.eat_by)) " +
            "SELECT f._id, f.version, f.name AS name, i.eatBy AS eatBy, i.amount AS amount FROM Food f " +
            "INNER JOIN least_item i ON i.of_type = f._id " +
            "ORDER BY eatBy")
    public abstract LiveData<List<FoodView>> getFoodByLocation(int location);

    @Query("SELECT f._id, f.version, f.name FROM Food f " +
            "INNER JOIN EanNumber n ON n.identifies = f._id " +
            "WHERE n.number = :s " +
            "LIMIT 1")
    public abstract LiveData<Food> getFoodByEanNumber(String s);

    @Query("SELECT f._id AS _id, f.version AS version, f.name AS name, count(*) AS amount " +
            "FROM Food f INNER JOIN FoodItem i ON f._id = i.of_type " +
            "WHERE f.name like :searchTerm " +
            "GROUP BY f.name " +
            "UNION " +
            "SELECT f._id AS _id, f.version AS version, f.name AS name, 0 AS amount " +
            "FROM Food f " +
            "WHERE f.name LIKE :searchTerm AND f._id NOT IN (SELECT DISTINCT of_type FROM FoodItem)")
    public abstract LiveData<List<FoodView>> getFoodBySubString(String searchTerm);
}
