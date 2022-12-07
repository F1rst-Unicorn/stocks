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

package de.njsm.stocks.client.business.entities.conflict;

import com.google.auto.value.AutoValue;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.LocationForListing;
import de.njsm.stocks.client.business.entities.ScaledUnitForListing;

import java.time.Period;
import java.util.Optional;

@AutoValue
public abstract class FoodEditConflictData implements Id<Food> {

    public abstract int errorId();

    public abstract int originalVersion();

    public abstract ConflictData<String> name();

    public abstract ConflictData<Boolean> toBuy();

    public abstract ConflictData<Period> expirationOffset();

    public abstract ConflictData<Optional<LocationForListing>> location();

    public abstract ConflictData<ScaledUnitForListing> storeUnit();

    public abstract ConflictData<String> description();

    public static FoodEditConflictData create(
            int errorId,
            int id,
            int originalVersion,
            String originalName,
            String remoteName,
            String localName,
            boolean originalToBuy,
            boolean remoteToBuy,
            boolean localToBuy,
            Period originalExpirationOffset,
            Period remoteExpirationOffset,
            Period localExpirationOffset,
            Optional<LocationForListing> originalLocation,
            Optional<LocationForListing> remoteLocation,
            Optional<LocationForListing> localLocation,
            ScaledUnitForListing originalStoreUnit,
            ScaledUnitForListing remoteStoreUnit,
            ScaledUnitForListing localStoreUnit,
            String originalDescription,
            String remoteDescription,
            String localDescription) {
        return new AutoValue_FoodEditConflictData(id, errorId, originalVersion,
                ConflictData.create(originalName, remoteName, localName),
                ConflictData.create(originalToBuy, remoteToBuy, localToBuy),
                ConflictData.create(originalExpirationOffset, remoteExpirationOffset, localExpirationOffset),
                ConflictData.create(originalLocation, remoteLocation, localLocation),
                ConflictData.create(originalStoreUnit, remoteStoreUnit, localStoreUnit),
                ConflictData.createMerging(originalDescription, remoteDescription, localDescription));
    }
}
