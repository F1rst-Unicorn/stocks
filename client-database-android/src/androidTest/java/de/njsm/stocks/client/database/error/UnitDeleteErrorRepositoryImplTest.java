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
import de.njsm.stocks.client.database.*;
import org.junit.Test;

import java.time.Instant;
import java.util.List;

import static de.njsm.stocks.client.database.BitemporalOperations.currentDelete;
import static de.njsm.stocks.client.database.Util.test;
import static de.njsm.stocks.client.database.Util.testList;
import static java.util.Collections.singletonList;

public class UnitDeleteErrorRepositoryImplTest extends AbstractErrorRepositoryImplTest {

    ErrorDetails recordError(StatusCodeException e) {
        UnitDbEntity unit = StandardEntities.unitDbEntity();
        UnitForDeletion data = UnitForDeletion.create(unit.id(), unit.version());
        UnitDeleteErrorDetails errorDetails = UnitDeleteErrorDetails.create(unit.id(), unit.name(), unit.abbreviation());
        stocksDatabase.synchronisationDao().writeUnits(singletonList(unit));
        errorRecorder.recordUnitDeleteError(e, data);
        return errorDetails;
    }

    @Override
    List<?> getErrorDetails() {
        return stocksDatabase.errorDao().getUnitDeletes();
    }

    @Test
    public void gettingErrorOfDeletedEntityWorks() {
        Instant editTime = Instant.EPOCH.plusSeconds(1);
        StatusCode statusCode = StatusCode.DATABASE_UNREACHABLE;
        StatusCodeException exception = new StatusCodeException(statusCode);
        UnitDbEntity unit = StandardEntities.unitDbEntity();
        UnitForDeletion unitForDeletion = UnitForDeletion.builder()
                .id(unit.id())
                .version(unit.version())
                .build();
        stocksDatabase.synchronisationDao().writeUnits(singletonList(unit));
        stocksDatabase.synchronisationDao().writeUnits(currentDelete(unit, editTime));
        errorRecorder.recordUnitDeleteError(exception, unitForDeletion);
        ErrorDetails data = UnitDeleteErrorDetails.create(unit.id(), unit.name(), unit.abbreviation());

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
        UnitDbEntity unit = StandardEntities.unitDbEntity();
        UnitForDeletion unitForDeletion = UnitForDeletion.builder()
                .id(unit.id())
                .version(unit.version())
                .build();
        stocksDatabase.synchronisationDao().writeUnits(singletonList(unit));
        stocksDatabase.synchronisationDao().writeUnits(BitemporalOperations.sequencedDeleteOfEntireTime(unit, editTime));
        stocksDatabase.synchronisationDao().insert(singletonList(UpdateDbEntity.create(EntityType.UNIT, Instant.EPOCH)));
        errorRecorder.recordUnitDeleteError(exception, unitForDeletion);
        ErrorDetails data = UnitDeleteErrorDetails.create(unit.id(), unit.name(), unit.abbreviation());

        test(uut.getNumberOfErrors()).assertValue(1);
        testList(uut.getErrors()).assertValue(v -> v.get(0).statusCode() == statusCode);
        testList(uut.getErrors()).assertValue(v -> v.get(0).errorDetails().equals(data));
        testList(uut.getErrors()).assertValue(v -> v.get(0).errorMessage().equals(exception.getMessage()));
    }
}
