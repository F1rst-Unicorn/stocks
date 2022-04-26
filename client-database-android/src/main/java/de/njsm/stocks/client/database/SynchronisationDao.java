/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.njsm.stocks.client.database;

import androidx.room.*;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public abstract class SynchronisationDao {

    @Query("select * from updates")
    abstract List<UpdateDbEntity> getAll();

    @Insert(onConflict = REPLACE)
    abstract void insert(List<UpdateDbEntity> updates);

    @Transaction
    void writeUpdates(List<UpdateDbEntity> locations) {
        delete();
        insert(locations);
    }

    @Query("delete from updates")
    abstract void delete();

    @Insert(onConflict = REPLACE)
    public abstract void writeLocations(List<LocationDbEntity> locations);

    @Transaction
    void synchroniseLocations(List<LocationDbEntity> locations) {
        deleteLocations();
        writeLocations(locations);
    }

    @Query("delete from location")
    abstract void deleteLocations();

    @Transaction
    void synchroniseUsers(List<UserDbEntity> data) {
        deleteUsers();
        writeUsers(data);
    }

    @Insert(onConflict = REPLACE)
    abstract void writeUsers(List<UserDbEntity> data);

    @Query("delete from user")
    abstract void deleteUsers();

    @Transaction
    void synchroniseUserDevices(List<UserDeviceDbEntity> data) {
        deleteUserDevices();
        writeUserDevices(data);
    }

    @Insert(onConflict = REPLACE)
    abstract void writeUserDevices(List<UserDeviceDbEntity> data);

    @Query("delete from user_device")
    abstract void deleteUserDevices();

    @Transaction
    void synchroniseFood(List<FoodDbEntity> data) {
        deleteFood();
        writeFood(data);
    }

    @Insert(onConflict = REPLACE)
    abstract void writeFood(List<FoodDbEntity> data);

    @Query("delete from food")
    abstract void deleteFood();


    @Transaction
    void synchroniseEanNumbers(List<EanNumberDbEntity> data) {
        deleteEanNumbers();
        writeEanNumbers(data);
    }

    @Insert(onConflict = REPLACE)
    abstract void writeEanNumbers(List<EanNumberDbEntity> data);

    @Query("delete from ean_number")
    abstract void deleteEanNumbers();
}
