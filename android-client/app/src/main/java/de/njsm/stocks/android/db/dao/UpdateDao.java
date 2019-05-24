package de.njsm.stocks.android.db.dao;

import androidx.room.*;
import de.njsm.stocks.android.db.entities.Update;


@Dao
public abstract class UpdateDao {

    @Query("SELECT * FROM Updates")
    public abstract Update[] getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void set(Update... u);

    @Transaction
    public void reset() {
        delete();
    }

    @Query("DELETE FROM Updates")
    abstract void delete();

}
