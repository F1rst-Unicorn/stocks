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
import de.njsm.stocks.client.database.UpdateDbEntity;
import de.njsm.stocks.client.database.UserDbEntity;
import org.junit.Test;

import java.time.Instant;
import java.util.List;

import static de.njsm.stocks.client.database.BitemporalOperations.currentDelete;
import static de.njsm.stocks.client.database.BitemporalOperations.sequencedDeleteOfEntireTime;
import static de.njsm.stocks.client.database.util.Util.test;
import static de.njsm.stocks.client.database.util.Util.testList;
import static java.util.Collections.singletonList;

public class UserDeleteErrorRepositoryImplTest extends AbstractErrorRepositoryImplTest {

    ErrorDetails recordError(StatusCodeException e) {
        UserDbEntity user = standardEntities.userDbEntity();
        stocksDatabase.synchronisationDao().writeUsers(singletonList(user));
        UserForDeletion data = UserForDeletion.create(user.id(), user.version());
        ErrorDetails errorDetails = UserDeleteErrorDetails.create(user.id(), user.name());
        errorRecorder.recordUserDeleteError(e, data);
        return errorDetails;
    }

    @Override
    List<?> getErrorDetails() {
        return stocksDatabase.errorDao().getUserDeletes();
    }

    @Test
    public void gettingErrorOfDeletedEntityWorks() {
        Instant editTime = Instant.EPOCH.plusSeconds(5);
        StatusCode statusCode = StatusCode.DATABASE_UNREACHABLE;
        StatusCodeException exception = new StatusCodeException(statusCode);
        UserDbEntity user = standardEntities.userDbEntity();
        stocksDatabase.synchronisationDao().writeUsers(singletonList(user));
        UserForDeletion data = UserForDeletion.create(user.id(), user.version());
        stocksDatabase.synchronisationDao().writeUsers(currentDelete(user, editTime));
        errorRecorder.recordUserDeleteError(exception, data);
        ErrorDetails errorDetails = UserDeleteErrorDetails.create(user.id(), user.name());

        test(uut.getNumberOfErrors()).assertValue(1);
        testList(uut.getErrors()).assertValue(v -> v.get(0).statusCode() == statusCode);
        testList(uut.getErrors()).assertValue(v -> v.get(0).errorDetails().equals(errorDetails));
        testList(uut.getErrors()).assertValue(v -> v.get(0).errorMessage().equals(exception.getMessage()));
    }

    @Test
    public void gettingErrorOfInvalidatedEntityWorks() {
        Instant editTime = Instant.EPOCH.plusSeconds(5);
        StatusCode statusCode = StatusCode.DATABASE_UNREACHABLE;
        StatusCodeException exception = new StatusCodeException(statusCode);
        UserDbEntity user = standardEntities.userDbEntity();
        stocksDatabase.synchronisationDao().writeUsers(singletonList(user));
        UserForDeletion data = UserForDeletion.create(user.id(), user.version());
        stocksDatabase.synchronisationDao().writeUsers(singletonList(user));
        stocksDatabase.synchronisationDao().writeUsers(sequencedDeleteOfEntireTime(user, editTime));
        stocksDatabase.synchronisationDao().insert(singletonList(UpdateDbEntity.create(EntityType.USER, Instant.EPOCH)));
        errorRecorder.recordUserDeleteError(exception, data);
        ErrorDetails errorDetails = UserDeleteErrorDetails.create(user.id(), user.name());

        test(uut.getNumberOfErrors()).assertValue(1);
        testList(uut.getErrors()).assertValue(v -> v.get(0).statusCode() == statusCode);
        testList(uut.getErrors()).assertValue(v -> v.get(0).errorDetails().equals(errorDetails));
        testList(uut.getErrors()).assertValue(v -> v.get(0).errorMessage().equals(exception.getMessage()));
    }
}
