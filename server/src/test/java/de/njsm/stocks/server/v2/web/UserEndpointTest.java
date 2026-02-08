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

package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.common.api.*;
import de.njsm.stocks.common.api.serialisers.InstantSerialiser;
import de.njsm.stocks.server.v2.business.UserManager;
import fj.data.Validation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class UserEndpointTest {

    private UserEndpoint uut;

    private UserManager userManager;

    @BeforeEach
    public void setup() {
        userManager = Mockito.mock(UserManager.class);

        uut = new UserEndpoint(userManager);
    }

    @AfterEach
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(userManager);
    }

    @Test
    public void getUsers() {
        Mockito.when(userManager.get(Instant.EPOCH, Instant.EPOCH)).thenReturn(Validation.success(emptyList()));

        var actual = (ListResponse<User>) uut.get(InstantSerialiser.serialize(Instant.EPOCH), InstantSerialiser.serialize(Instant.EPOCH));

        assertEquals(StatusCode.SUCCESS, actual.getStatus());
        assertEquals(0, actual.data.size());
        Mockito.verify(userManager).get(Instant.EPOCH, Instant.EPOCH);
    }

    @Test
    public void getUsersFromInvalidStartingPoint() {

        var actual = uut.get("invalid", "invalid");

        assertEquals(StatusCode.INVALID_ARGUMENT, actual.getStatus());
    }

    @Test
    public void addingInvalidNameIsRejected() {
        Response result = uut.putUser(null);
        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void addUserContainingDollarIsRejected() {
        String name = "John$1";
        Response result = uut.putUser(name);
        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void addUserContainingEqualSignIsRejected() {
        String name = "John=1";
        Response result = uut.putUser(name);
        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void validAddingIsSuccessful() {
        String name = "user";
        Mockito.when(userManager.addReturningId(any())).thenReturn(Validation.success(1));

        Response result = uut.putUser(name);

        assertEquals(StatusCode.SUCCESS, result.getStatus());
        Mockito.verify(userManager).addReturningId(UserForInsertion.builder()
                .name(name)
                .build());
    }

    @Test
    public void deletingInvalidVersionIsRejected() {

        Response result = uut.delete(1, -1);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void deletingInvalidIdIsRejected() {

        Response result = uut.delete(0, 1);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void validDeletingIsSuccessful() {
        Mockito.when(userManager.delete(any())).thenReturn(StatusCode.SUCCESS);

        Response result = uut.delete(1, 2);

        assertEquals(StatusCode.SUCCESS, result.getStatus());
        Mockito.verify(userManager).delete(UserForDeletion.builder()
                .id(1)
                .version(2)
                .build());
    }
}
