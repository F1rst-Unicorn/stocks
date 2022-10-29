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
import de.njsm.stocks.client.business.Localiser;
import de.njsm.stocks.client.business.entities.LocationForListing;
import de.njsm.stocks.client.business.entities.ScaledUnitForListing;
import de.njsm.stocks.client.business.entities.UnitForListing;
import de.njsm.stocks.client.business.entities.conflict.*;
import de.njsm.stocks.client.database.*;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class ConflictRepositoryImpl implements ConflictRepository {

    private final ErrorDao errorDao;

    private final Localiser localiser;

    @Inject
    ConflictRepositoryImpl(ErrorDao errorDao, Localiser localiser) {
        this.errorDao = errorDao;
        this.localiser = localiser;
    }

    @Override
    public Observable<LocationEditConflictData> getLocationEditConflict(long errorId) {
        return errorDao.observeError(errorId).map(error -> {
            if (error.action() != ErrorEntity.Action.EDIT_LOCATION)
                throw new IllegalArgumentException("error " + errorId + " does not belong to " + ErrorEntity.Action.EDIT_LOCATION + " but to " + error.action());

            LocationEditEntity locationEditEntity = errorDao.getLocationEdit(error.dataId());
            LocationDbEntity original = errorDao.getCurrentLocationAsKnownAt(locationEditEntity.location().id(), locationEditEntity.location().transactionTime());
            LocationDbEntity remote = errorDao.getCurrentLocationAsKnownAt(locationEditEntity.location().id(), locationEditEntity.executionTime());

            return LocationEditConflictData.create(error.id(), locationEditEntity.location().id(), locationEditEntity.version(),
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
            UnitDbEntity original = errorDao.getCurrentUnitAsKnownAt(unitEditEntity.unit().id(), unitEditEntity.unit().transactionTime());
            UnitDbEntity remote = errorDao.getCurrentUnitAsKnownAt(unitEditEntity.unit().id(), unitEditEntity.executionTime());

            return UnitEditConflictData.create(error.id(), unitEditEntity.unit().id(), unitEditEntity.version(),
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
            ScaledUnitDbEntity original = errorDao.getCurrentScaledUnitAsKnownAt(scaledUnitEditEntity.scaledUnit().id(), scaledUnitEditEntity.scaledUnit().transactionTime());
            ScaledUnitDbEntity remote = errorDao.getCurrentScaledUnitAsKnownAt(scaledUnitEditEntity.scaledUnit().id(), scaledUnitEditEntity.executionTime());

            UnitDbEntity originalUnit = errorDao.getCurrentUnitAsKnownAt(original.unit(), scaledUnitEditEntity.scaledUnit().transactionTime());
            UnitDbEntity remoteUnit = errorDao.getCurrentUnitAsKnownAt(remote.unit(), scaledUnitEditEntity.executionTime());
            UnitDbEntity localUnit = errorDao.getCurrentUnitAsKnownAt(scaledUnitEditEntity.unit().id(), scaledUnitEditEntity.unit().transactionTime());

            return ScaledUnitEditConflictData.create(error.id(), scaledUnitEditEntity.scaledUnit().id(), scaledUnitEditEntity.version(),
                    original.scale(), remote.scale(), scaledUnitEditEntity.scale(),
                    getUnitForListingFromDbEntity(originalUnit),
                    getUnitForListingFromDbEntity(remoteUnit),
                    getUnitForListingFromDbEntity(localUnit));
        });
    }

    @Override
    public Observable<FoodEditConflictData> getFoodEditConflict(long errorId) {
        return errorDao.observeError(errorId).map(error -> {
            if (error.action() != ErrorEntity.Action.EDIT_FOOD)
                throw new IllegalArgumentException("error " + errorId + " does not belong to " + ErrorEntity.Action.EDIT_FOOD + " but to " + error.action());

            FoodEditEntity local = errorDao.getFoodEdit(error.dataId());
            FoodDbEntity original = errorDao.getCurrentFoodAsKnownAt(local.food().id(), local.food().transactionTime());
            FoodDbEntity remote = errorDao.getCurrentFoodAsKnownAt(local.food().id(), local.executionTime());

            Optional<LocationForListing> originalLocation = ofNullable(original.location()).map(v -> errorDao.getCurrentLocationAsKnownAt(v, local.location().transactionTime()))
                    .map(v -> LocationForListing.create(v.id(), v.name()));
            Optional<LocationForListing> remoteLocation = ofNullable(remote.location()).map(v -> errorDao.getCurrentLocationAsKnownAt(v, local.executionTime()))
                    .map(v -> LocationForListing.create(v.id(), v.name()));
            Optional<LocationForListing> localLocation = local.location().maybe().map(v -> errorDao.getCurrentLocationAsKnownAt(v.id(), v.transactionTime()))
                    .map(v -> LocationForListing.create(v.id(), v.name()));

            ScaledUnitDbEntity originalScaledUnit = errorDao.getCurrentScaledUnitAsKnownAt(original.storeUnit(), local.storeUnit().transactionTime());
            ScaledUnitDbEntity remoteScaledUnit = errorDao.getCurrentScaledUnitAsKnownAt(remote.storeUnit(), local.executionTime());
            ScaledUnitDbEntity localScaledUnit = errorDao.getCurrentScaledUnitAsKnownAt(local.storeUnit().id(), local.storeUnit().transactionTime());

            UnitDbEntity originalUnit = errorDao.getCurrentUnitAsKnownAt(originalScaledUnit.unit(), local.storeUnit().transactionTime());
            UnitDbEntity remoteUnit = errorDao.getCurrentUnitAsKnownAt(remoteScaledUnit.unit(), local.executionTime());
            UnitDbEntity localUnit = errorDao.getCurrentUnitAsKnownAt(localScaledUnit.unit(), local.storeUnit().transactionTime());

            return FoodEditConflictData.create(error.id(), local.food().id(), local.version(),
                    original.name(), remote.name(), local.name(),
                    original.expirationOffset(), remote.expirationOffset(), local.expirationOffset(),
                    originalLocation, remoteLocation, localLocation,
                    ScaledUnitForListing.create(originalScaledUnit.id(), originalUnit.abbreviation(), originalScaledUnit.scale()),
                    ScaledUnitForListing.create(remoteScaledUnit.id(), remoteUnit.abbreviation(), remoteScaledUnit.scale()),
                    ScaledUnitForListing.create(localScaledUnit.id(), localUnit.abbreviation(), localScaledUnit.scale()),
                    original.description(), remote.description(), local.description());
        });
    }

    @Override
    public Observable<FoodItemEditConflictData> getFoodItemEditConflict(long errorId) {
        return errorDao.observeError(errorId).map(error -> {
            if (error.action() != ErrorEntity.Action.EDIT_FOOD_ITEM)
                throw new IllegalArgumentException("error " + errorId + " does not belong to " + ErrorEntity.Action.EDIT_FOOD_ITEM + " but to " + error.action());

            FoodItemEditEntity local = errorDao.getFoodItemEdit(error.dataId());
            FoodItemDbEntity original = errorDao.getCurrentFoodItemAsKnownAt(local.foodItem().id(), local.foodItem().transactionTime());
            FoodItemDbEntity remote = errorDao.getCurrentFoodItemAsKnownAt(local.foodItem().id(), local.executionTime());

            FoodDbEntity food = errorDao.getCurrentFoodAsKnownAt(original.ofType(), local.foodItem().transactionTime());

            LocationDbEntity originalLocation = errorDao.getCurrentLocationAsKnownAt(original.storedIn(), local.storedIn().transactionTime());
            LocationDbEntity remoteLocation = errorDao.getCurrentLocationAsKnownAt(remote.storedIn(), local.executionTime());
            LocationDbEntity localLocation = errorDao.getCurrentLocationAsKnownAt(local.storedIn().id(), local.storedIn().transactionTime());

            ScaledUnitDbEntity originalScaledUnit = errorDao.getCurrentScaledUnitAsKnownAt(original.unit(), local.unit().transactionTime());
            ScaledUnitDbEntity remoteScaledUnit = errorDao.getCurrentScaledUnitAsKnownAt(remote.unit(), local.executionTime());
            ScaledUnitDbEntity localScaledUnit = errorDao.getCurrentScaledUnitAsKnownAt(local.unit().id(), local.unit().transactionTime());

            UnitDbEntity originalUnit = errorDao.getCurrentUnitAsKnownAt(originalScaledUnit.unit(), local.unit().transactionTime());
            UnitDbEntity remoteUnit = errorDao.getCurrentUnitAsKnownAt(remoteScaledUnit.unit(), local.executionTime());
            UnitDbEntity localUnit = errorDao.getCurrentUnitAsKnownAt(localScaledUnit.unit(), local.unit().transactionTime());

            return FoodItemEditConflictData.create(error.id(), local.foodItem().id(), local.version(), food.name(),
                    localiser.toLocalDate(original.eatBy()),
                    localiser.toLocalDate(remote.eatBy()),
                    localiser.toLocalDate(local.eatBy()),
                    LocationForListing.create(originalLocation.id(), originalLocation.name()),
                    LocationForListing.create(remoteLocation.id(), remoteLocation.name()),
                    LocationForListing.create(localLocation.id(), localLocation.name()),
                    ScaledUnitForListing.create(originalScaledUnit.id(), originalUnit.abbreviation(), originalScaledUnit.scale()),
                    ScaledUnitForListing.create(remoteScaledUnit.id(), remoteUnit.abbreviation(), remoteScaledUnit.scale()),
                    ScaledUnitForListing.create(localScaledUnit.id(), localUnit.abbreviation(), localScaledUnit.scale()));
        });
    }

    private UnitForListing getUnitForListingFromDbEntity(UnitDbEntity dbEntity) {
        return UnitForListing.create(dbEntity.id(), dbEntity.name(), dbEntity.abbreviation());
    }
}
