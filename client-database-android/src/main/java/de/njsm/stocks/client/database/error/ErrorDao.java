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

package de.njsm.stocks.client.database.error;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;

@Dao
public abstract class ErrorDao {

    @Query("select * from status_code_error")
    abstract List<StatusCodeExceptionEntity> getStatusCodeErrors();

    @Query("select * from subsystem_error")
    abstract List<SubsystemExceptionEntity> getSubsystemErrors();

    @Insert
    abstract long insert(StatusCodeExceptionEntity synchronisationError);

    @Insert
    abstract long insert(SubsystemExceptionEntity error);

    @Query("select * from location_to_add")
    abstract List<LocationAddEntity> getLocationAdds();

    @Insert
    abstract long insert(LocationAddEntity locationAddEntity);

    @Insert
    abstract void insert(ErrorEntity locationAddEntity);

    @Query("select * from error")
    abstract List<ErrorEntity> getErrors();

    @Query("select count(*) from error")
    abstract Observable<Integer> getNumberOfErrors();

    @Query("select * from error")
    abstract Observable<List<ErrorEntity>> observeErrors();

    @Query("select * from location_to_add where _id = :id")
    abstract LocationAddEntity getLocationAdd(long id);

    @Query("select * from status_code_error where _id = :id")
    abstract StatusCodeExceptionEntity getStatusCodeException(long id);

    @Query("select * from subsystem_error where _id = :id")
    abstract SubsystemExceptionEntity getSubsystemException(long id);

    @Query("delete from error where _id = :id")
    abstract void deleteError(long id);

    @Query("delete from status_code_error where _id = :id")
    abstract void deleteStatusCodeException(long id);

    @Query("delete from subsystem_error where _id = :id")
    abstract void deleteSubsystemException(long id);

    @Query("delete from location_to_add where _id = :id")
    abstract void deleteLocationAdd(long id);

    @Query("select * from error where _id = :id")
    abstract ErrorEntity getError(long id);

    @Query("select * from error where _id = :id")
    abstract Observable<ErrorEntity> observeError(long id);

    @Query("select * from location_to_delete")
    abstract List<LocationDeleteEntity> getLocationDeletes();

    @Insert
    abstract long insert(LocationDeleteEntity locationDeleteEntity);

    @Query("select * from location_to_delete where _id = :id")
    abstract LocationDeleteEntity getLocationDelete(long id);

    @Query("delete from location_to_delete where _id = :id")
    abstract void deleteLocationDelete(long id);

    @Query("select name from current_location where _id = :id")
    abstract String getLocationName(int id);

    @Query("select * from location_to_edit")
    abstract List<LocationEditEntity> getLocationEdits();

    @Insert
    abstract long insert(LocationEditEntity locationEditEntity);

    @Query("select * from location_to_edit where _id = :id")
    abstract LocationEditEntity getLocationEdit(Long id);

    @Query("delete from location_to_edit where _id = :id")
    abstract void deleteLocationEdit(long id);
}
