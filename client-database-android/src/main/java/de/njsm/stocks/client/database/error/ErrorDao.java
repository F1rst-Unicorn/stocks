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
import de.njsm.stocks.client.business.entities.EntityType;
import de.njsm.stocks.client.database.LocationDbEntity;
import de.njsm.stocks.client.database.PreservedId;
import de.njsm.stocks.client.database.ScaledUnitDbEntity;
import de.njsm.stocks.client.database.UnitDbEntity;
import io.reactivex.rxjava3.core.Observable;

import java.time.Instant;
import java.util.List;

import static de.njsm.stocks.client.database.StocksDatabase.DATABASE_INFINITY_STRING_SQL;
import static de.njsm.stocks.client.database.StocksDatabase.NOW;

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

    @Query("select * from location_to_add where id = :id")
    abstract LocationAddEntity getLocationAdd(long id);

    @Query("select * from status_code_error where id = :id")
    abstract StatusCodeExceptionEntity getStatusCodeException(long id);

    @Query("select * from subsystem_error where id = :id")
    abstract SubsystemExceptionEntity getSubsystemException(long id);

    @Query("delete from error where id = :id")
    abstract void deleteError(long id);

    @Query("delete from status_code_error where id = :id")
    abstract void deleteStatusCodeException(long id);

    @Query("delete from subsystem_error where id = :id")
    abstract void deleteSubsystemException(long id);

    @Query("delete from location_to_add where id = :id")
    abstract void deleteLocationAdd(long id);

    @Query("select * from error where id = :id")
    abstract ErrorEntity getError(long id);

    @Query("select * from error where id = :id")
    abstract Observable<ErrorEntity> observeError(long id);

    @Query("select * from location_to_delete")
    abstract List<LocationDeleteEntity> getLocationDeletes();

    @Insert
    abstract long insert(LocationDeleteEntity locationDeleteEntity);

    @Query("select * from location_to_delete where id = :id")
    abstract LocationDeleteEntity getLocationDelete(long id);

    @Query("delete from location_to_delete where id = :id")
    abstract void deleteLocationDelete(long id);

    @Query("select * from location_to_edit")
    abstract List<LocationEditEntity> getLocationEdits();

    @Insert
    abstract long insert(LocationEditEntity locationEditEntity);

    @Query("select * from location_to_edit where id = :id")
    abstract LocationEditEntity getLocationEdit(Long id);

    @Query("delete from location_to_edit where id = :id")
    abstract void deleteLocationEdit(long id);

    LocationDbEntity getLocationByValidOrTransactionTime(PreservedId id) {
        LocationDbEntity location = getCurrentLocation(id.id());
        if (location == null) {
            location = getLatestLocationAsBestKnown(id.id());
        }
        if (location == null) {
            location = getCurrentLocationAsKnownAt(id.id(), id.transactionTime());
        }
        return location;
    }

    @Query("select * " +
            "from current_location " +
            "where id = :locationId")
    abstract LocationDbEntity getCurrentLocation(int locationId);

    @Query("select * " +
            "from location " +
            "where id = :id " +
            "and transaction_time_start <= :transactionTime " +
            "and :transactionTime < transaction_time_end " +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from location " +
            "   where valid_time_start <= " + NOW +
            "   and transaction_time_start <= :transactionTime " +
            "   and :transactionTime < transaction_time_end " +
            ")")
    abstract LocationDbEntity getCurrentLocationAsKnownAt(int id, Instant transactionTime);

    @Query("select * " +
            "from location " +
            "where id = :id " +
            "and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from location " +
            "   where valid_time_start <= " + NOW +
            "   and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            ")")
    abstract LocationDbEntity getLatestLocationAsBestKnown(int id);

    @Query("select * from unit_to_add")
    abstract List<UnitAddEntity> getUnitAdds();

    @Insert
    abstract long insert(UnitAddEntity unitAddEntity);

    @Query("delete from unit_to_add where id = :id")
    abstract void deleteUnitAdd(long id);

    @Query("select * from unit_to_add where id = :id")
    abstract UnitAddEntity getUnitAdd(long id);

    @Query("select * from unit_to_delete")
    abstract List<UnitDeleteEntity> getUnitDeletes();

    @Insert
    abstract long insert(UnitDeleteEntity entity);

    @Query("delete from unit_to_delete where id = :id")
    abstract void deleteUnitDelete(long id);

    @Query("select * from unit_to_delete where id = :id")
    abstract UnitDeleteEntity getUnitDelete(long id);

    UnitDbEntity getUnitByValidOrTransactionTime(PreservedId id) {
        UnitDbEntity unit = getCurrentUnit(id.id());
        if (unit == null) {
            unit = getLatestUnitAsBestKnown(id.id());
        }
        if (unit == null) {
            unit = getCurrentUnitAsKnownAt(id.id(), id.transactionTime());
        }
        return unit;
    }

    @Query("select * " +
            "from current_unit " +
            "where id = :unitId")
    abstract UnitDbEntity getCurrentUnit(int unitId);

    @Query("select * " +
            "from unit " +
            "where id = :id " +
            "and transaction_time_start <= :transactionTime " +
            "and :transactionTime < transaction_time_end " +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from unit " +
            "   where id = :id" +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_start <= :transactionTime " +
            "   and :transactionTime < transaction_time_end " +
            ")")
    abstract UnitDbEntity getCurrentUnitAsKnownAt(int id, Instant transactionTime);

    @Query("select * " +
            "from unit " +
            "where id = :id " +
            "and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from unit " +
            "   where id = :id" +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            ")")
    abstract UnitDbEntity getLatestUnitAsBestKnown(int id);

    @Query("select * from unit_to_edit")
    abstract List<UnitEditEntity> getUnitEdits();

    @Insert
    abstract long insert(UnitEditEntity entity);

    @Query("delete from unit_to_edit where id = :id")
    abstract void deleteUnitEdit(long id);

    @Query("select * from unit_to_edit where id = :id")
    abstract UnitEditEntity getUnitEdit(long id);

    @Query("select * from scaled_unit_to_add")
    abstract List<ScaledUnitAddEntity> getScaledUnitAdds();

    @Query("select * from scaled_unit_to_add where id = :id")
    abstract ScaledUnitAddEntity getScaledUnitAdd(Long id);

    @Query("delete from scaled_unit_to_add where id = :id")
    abstract void deleteScaledUnitAdd(Long id);

    @Insert
    abstract long insert(ScaledUnitAddEntity entity);

    @Query("select * from scaled_unit_to_edit")
    abstract List<ScaledUnitEditEntity> getScaledUnitEdits();

    @Insert
    abstract long insert(ScaledUnitEditEntity entity);

    @Query("delete from scaled_unit_to_edit where id = :id")
    abstract void deleteScaledUnitEdit(long id);

    @Query("select * " +
            "from scaled_unit_to_edit " +
            "where id = :id")
    abstract ScaledUnitEditEntity getScaledUnitEdit(Long id);

    @Query("select * " +
            "from scaled_unit_to_delete")
    abstract List<ScaledUnitDeleteEntity> getScaledUnitDeletes();

    @Insert
    abstract long insert(ScaledUnitDeleteEntity entity);

    @Query("select * " +
            "from scaled_unit_to_delete " +
            "where id = :id")
    abstract ScaledUnitDeleteEntity getScaledUnitDelete(long id);

    ScaledUnitDbEntity getScaledUnitByValidOrTransactionTime(PreservedId id) {
        ScaledUnitDbEntity unit = getCurrentScaledUnit(id.id());
        if (unit == null) {
            unit = getLatestScaledUnitAsBestKnown(id.id());
        }
        if (unit == null) {
            unit = getCurrentScaledUnitAsKnownAt(id.id(), id.transactionTime());
        }
        return unit;
    }

    @Query("select * " +
            "from current_scaled_unit " +
            "where id = :unitId")
    abstract ScaledUnitDbEntity getCurrentScaledUnit(int unitId);

    @Query("select * " +
            "from scaled_unit " +
            "where id = :id " +
            "and transaction_time_start <= :transactionTime " +
            "and :transactionTime < transaction_time_end " +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from scaled_unit " +
            "   where id = :id" +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_start <= :transactionTime " +
            "   and :transactionTime < transaction_time_end " +
            ")")
    abstract ScaledUnitDbEntity getCurrentScaledUnitAsKnownAt(int id, Instant transactionTime);

    @Query("select * " +
            "from scaled_unit " +
            "where id = :id " +
            "and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from scaled_unit " +
            "   where id = :id" +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            ")")
    abstract ScaledUnitDbEntity getLatestScaledUnitAsBestKnown(int id);

    @Query("delete from scaled_unit_to_delete where id = :id")
    abstract void deleteScaledUnitDelete(long id);

    @Query("select last_update " +
            "from updates " +
            "where name = :entityType")
    abstract Instant getTransactionTimeOf(EntityType entityType);
}
