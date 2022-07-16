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

import de.njsm.stocks.client.business.ConflictRepository;
import de.njsm.stocks.client.business.entities.UnitForListing;
import de.njsm.stocks.client.business.entities.conflict.LocationEditConflictData;
import de.njsm.stocks.client.business.entities.conflict.ScaledUnitEditConflictData;
import de.njsm.stocks.client.business.entities.conflict.UnitEditConflictData;
import de.njsm.stocks.client.database.LocationDbEntity;
import de.njsm.stocks.client.database.ScaledUnitDbEntity;
import de.njsm.stocks.client.database.UnitDbEntity;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class ConflictRepositoryImpl implements ConflictRepository {

    private final ErrorDao errorDao;

    @Inject
    ConflictRepositoryImpl(ErrorDao errorDao) {
        this.errorDao = errorDao;
    }

    @Override
    public Observable<LocationEditConflictData> getLocationEditConflict(long errorId) {
        return errorDao.observeError(errorId).map(error -> {
            if (error.action() != ErrorEntity.Action.EDIT_LOCATION)
                throw new IllegalArgumentException("error " + errorId + " does not belong to " + ErrorEntity.Action.EDIT_LOCATION + " but to " + error.action());

            LocationEditEntity locationEditEntity = errorDao.getLocationEdit(error.dataId());
            LocationDbEntity original = errorDao.getCurrentLocationAsKnownAt(locationEditEntity.locationId(), locationEditEntity.transactionTime());
            LocationDbEntity remote = errorDao.getCurrentLocationAsKnownAt(locationEditEntity.locationId(), locationEditEntity.executionTime());

            return LocationEditConflictData.create(error.id(), locationEditEntity.locationId(), locationEditEntity.version(),
                    original.name(), remote.name(), locationEditEntity.name(),
                    original.description(), remote.description(), locationEditEntity.description());
        });
    }

    @Override
    public Observable<UnitEditConflictData> getUnitEditConflict(long errorId) {
        return errorDao.observeError(errorId).map(error -> {
            if (error.action() != ErrorEntity.Action.EDIT_UNIT)
                throw new IllegalArgumentException("error " + errorId + " does not belong to " + ErrorEntity.Action.EDIT_UNIT + " but to " + error.action());

            UnitEditEntity unitEditEntity = errorDao.getUnitEdit(error.dataId());
            UnitDbEntity original = errorDao.getCurrentUnitAsKnownAt(unitEditEntity.unitId(), unitEditEntity.transactionTime());
            UnitDbEntity remote = errorDao.getCurrentUnitAsKnownAt(unitEditEntity.unitId(), unitEditEntity.executionTime());

            return UnitEditConflictData.create(error.id(), unitEditEntity.unitId(), unitEditEntity.version(),
                    original.name(), remote.name(), unitEditEntity.name(),
                    original.abbreviation(), remote.abbreviation(), unitEditEntity.abbreviation());
        });
    }

    @Override
    public Observable<ScaledUnitEditConflictData> getScaledUnitEditConflict(long errorId) {
        return errorDao.observeError(errorId).map(error -> {
            if (error.action() != ErrorEntity.Action.EDIT_SCALED_UNIT)
                throw new IllegalArgumentException("error " + errorId + " does not belong to " + ErrorEntity.Action.EDIT_SCALED_UNIT + " but to " + error.action());

            ScaledUnitEditEntity scaledUnitEditEntity = errorDao.getScaledUnitEdit(error.dataId());
            ScaledUnitDbEntity original = errorDao.getCurrentScaledUnitAsKnownAt(scaledUnitEditEntity.scaledUnitId(), scaledUnitEditEntity.transactionTime());
            ScaledUnitDbEntity remote = errorDao.getCurrentScaledUnitAsKnownAt(scaledUnitEditEntity.scaledUnitId(), scaledUnitEditEntity.executionTime());

            UnitDbEntity originalUnit = errorDao.getCurrentUnitAsKnownAt(original.unit(), scaledUnitEditEntity.transactionTime());
            UnitDbEntity remoteUnit = errorDao.getCurrentUnitAsKnownAt(remote.unit(), scaledUnitEditEntity.executionTime());
            UnitDbEntity localUnit = errorDao.getCurrentUnitAsKnownAt(scaledUnitEditEntity.unit(), scaledUnitEditEntity.executionTime());

            return ScaledUnitEditConflictData.create(error.id(), scaledUnitEditEntity.scaledUnitId(), scaledUnitEditEntity.version(),
                    original.scale(), remote.scale(), scaledUnitEditEntity.scale(),
                    getUnitForListingFromDbEntity(originalUnit),
                    getUnitForListingFromDbEntity(remoteUnit),
                    getUnitForListingFromDbEntity(localUnit));
        });
    }

    private UnitForListing getUnitForListingFromDbEntity(UnitDbEntity dbEntity) {
        return UnitForListing.create(dbEntity.id(), dbEntity.name(), dbEntity.abbreviation());
    }
}
