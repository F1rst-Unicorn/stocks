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

import de.njsm.stocks.client.business.StatusCodeException;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.database.LocationDbEntity;
import de.njsm.stocks.client.database.UpdateDbEntity;
import org.junit.Test;

import java.time.Instant;
import java.util.List;

import static de.njsm.stocks.client.database.BitemporalOperations.currentDelete;
import static de.njsm.stocks.client.database.BitemporalOperations.sequencedDeleteOfEntireTime;
import static de.njsm.stocks.client.database.util.Util.test;
import static de.njsm.stocks.client.database.util.Util.testList;
import static java.util.Collections.singletonList;

public class LocationDeleteErrorRepositoryImplTest extends AbstractErrorRepositoryImplTest {

    ErrorDetails recordError(StatusCodeException e) {
        LocationDbEntity location = standardEntities.locationDbEntity();
        LocationForDeletion locationForDeletion = LocationForDeletion.builder()
                .id(location.id())
                .version(location.version())
                .build();
        stocksDatabase.synchronisationDao().writeLocations(singletonList(location));
        errorRecorder.recordLocationDeleteError(e, locationForDeletion);
        return LocationDeleteErrorDetails.create(location.id(), location.name());
    }

    @Override
    List<?> getErrorDetails() {
        return stocksDatabase.errorDao().getLocationDeletes();
    }

    @Test
    public void gettingErrorOfDeletedEntityWorks() {
        Instant editTime = Instant.EPOCH.plusSeconds(1);
        StatusCode statusCode = StatusCode.DATABASE_UNREACHABLE;
        StatusCodeException exception = new StatusCodeException(statusCode);
        LocationDbEntity location = standardEntities.locationDbEntity();
        LocationForDeletion locationForDeletion = LocationForDeletion.builder()
                .id(location.id())
                .version(location.version())
                .build();
        stocksDatabase.synchronisationDao().writeLocations(singletonList(location));
        stocksDatabase.synchronisationDao().writeLocations(currentDelete(location, editTime));
        errorRecorder.recordLocationDeleteError(exception, locationForDeletion);
        ErrorDetails data = LocationDeleteErrorDetails.create(location.id(), location.name());

        test(uut.getNumberOfErrors()).assertValue(1);
        testList(uut.getErrors()).assertValue(v -> v.get(0).statusCode() == statusCode);
        testList(uut.getErrors()).assertValue(v -> v.get(0).errorDetails().equals(data));
        testList(uut.getErrors()).assertValue(v -> v.get(0).errorMessage().equals(exception.getMessage()));
    }

    @Test
    public void gettingErrorOfInvalidatedEntityWorks() {
        Instant editTime = Instant.now();
        StatusCode statusCode = StatusCode.DATABASE_UNREACHABLE;
        StatusCodeException exception = new StatusCodeException(statusCode);
        LocationDbEntity location = standardEntities.locationDbEntity();
        LocationForDeletion locationForDeletion = LocationForDeletion.builder()
                .id(location.id())
                .version(location.version())
                .build();
        stocksDatabase.synchronisationDao().writeLocations(singletonList(location));
        stocksDatabase.synchronisationDao().writeLocations(sequencedDeleteOfEntireTime(location, editTime));
        stocksDatabase.synchronisationDao().insert(singletonList(UpdateDbEntity.create(EntityType.LOCATION, Instant.EPOCH)));
        errorRecorder.recordLocationDeleteError(exception, locationForDeletion);
        ErrorDetails data = LocationDeleteErrorDetails.create(location.id(), location.name());

        test(uut.getNumberOfErrors()).assertValue(1);
        testList(uut.getErrors()).assertValue(v -> v.get(0).statusCode() == statusCode);
        testList(uut.getErrors()).assertValue(v -> v.get(0).errorDetails().equals(data));
        testList(uut.getErrors()).assertValue(v -> v.get(0).errorMessage().equals(exception.getMessage()));
    }
}
