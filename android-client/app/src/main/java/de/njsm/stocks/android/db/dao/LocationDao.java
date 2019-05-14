package de.njsm.stocks.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import de.njsm.stocks.android.db.entities.Location;

import java.util.List;

@Dao
public abstract class LocationDao {

    @Query("SELECT * FROM Location")
    public abstract LiveData<List<Location>> getAll();

    @Query("SELECT * FROM Location WHERE _id = :locationId")
    public abstract LiveData<Location> getLocation(int locationId);

    @Transaction
    public void synchronise(Location[] locations) {
        delete();
        insert(locations);
    }

    @Insert
    abstract void insert(Location[] locations);

    @Query("DELETE FROM Location")
    abstract void delete();
}
