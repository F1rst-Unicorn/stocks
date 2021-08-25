/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.common.api.*;
import de.njsm.stocks.server.v2.db.FoodItemHandler;
import de.njsm.stocks.server.v2.db.UserDeviceHandler;
import de.njsm.stocks.server.v2.db.UserHandler;
import de.njsm.stocks.server.v2.web.PrincipalFilterTest;
import fj.data.Validation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.ws.rs.container.AsyncResponse;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;

public class UserManagerTest {

    private UserManager uut;

    private UserHandler userDbHandler;

    private UserDeviceHandler deviceHandler;

    private FoodItemHandler foodItemHandler;

    @BeforeEach
    public void setup() {
        userDbHandler = Mockito.mock(UserHandler.class);
        deviceHandler = Mockito.mock(UserDeviceHandler.class);
        foodItemHandler = Mockito.mock(FoodItemHandler.class);

        uut = new UserManager(userDbHandler, deviceHandler, foodItemHandler);
        uut.setPrincipals(TEST_USER);
    }

    @AfterEach
    public void tearDown() {
        Mockito.verify(userDbHandler).setPrincipals(TEST_USER);
        Mockito.verify(deviceHandler).setPrincipals(TEST_USER);
        Mockito.verify(foodItemHandler).setPrincipals(TEST_USER);
        Mockito.verifyNoMoreInteractions(userDbHandler);
        Mockito.verifyNoMoreInteractions(deviceHandler);
        Mockito.verifyNoMoreInteractions(foodItemHandler);
    }

    @Test
    public void getUsersWorks() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);
        Mockito.when(userDbHandler.get(false, Instant.EPOCH)).thenReturn(Validation.success(Stream.empty()));
        Mockito.when(userDbHandler.commit()).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, Stream<User>> result = uut.get(r, false, Instant.EPOCH);

        assertTrue(result.isSuccess());
        Mockito.verify(userDbHandler).get(false, Instant.EPOCH);
        Mockito.verify(userDbHandler).setReadOnly();
    }

    @Test
    public void successfulAddingWorks() {
        UserForInsertion input = new UserForInsertion("fdsa");
        Mockito.when(userDbHandler.add(any())).thenReturn(StatusCode.SUCCESS);
        Mockito.when(userDbHandler.commit()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.add(input);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(userDbHandler).add(input);
        Mockito.verify(userDbHandler).commit();
    }

    @Test
    public void failingAddingWorks() {
        UserForInsertion input = new UserForInsertion("fdsa");
        Mockito.when(userDbHandler.add(any())).thenReturn(StatusCode.DATABASE_UNREACHABLE);

        StatusCode result = uut.add(input);

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
        Mockito.verify(userDbHandler).add(input);
        Mockito.verify(userDbHandler).rollback();
    }

    @Test
    public void deleteWithFailingDeviceRetrieval() {
        StatusCode code = StatusCode.DATABASE_UNREACHABLE;
        UserForDeletion input = new UserForDeletion(1, 2);
        Mockito.when(deviceHandler.getDevicesOfUser(input)).thenReturn(Validation.fail(code));

        StatusCode result = uut.delete(input);

        assertEquals(code, result);
        Mockito.verify(deviceHandler).getDevicesOfUser(input);
        Mockito.verify(userDbHandler).rollback();
    }

    @Test
    public void deleteWithFailingUserTransfer() {
        StatusCode code = StatusCode.DATABASE_UNREACHABLE;
        List<Identifiable<UserDevice>> devices = new LinkedList<>();
        UserForDeletion input = new UserForDeletion(1, 2);
        devices.add(UserDeviceForGetting.builder()
                .id(1)
                .version(2)
                .name("fdsa")
                .belongsTo(input.id())
                .build());
        devices.add(UserDeviceForGetting.builder()
                .id(2)
                .version(2)
                .name("fdsa")
                .belongsTo(input.id())
                .build());
        Mockito.when(deviceHandler.getDevicesOfUser(input)).thenReturn(Validation.success(devices));
        Mockito.when(foodItemHandler.transferFoodItems(input, PrincipalFilterTest.TEST_USER.toUser(), devices, PrincipalFilterTest.TEST_USER.toDevice()))
                .thenReturn(code);

        StatusCode result = uut.delete(input);

        assertEquals(code, result);
        Mockito.verify(deviceHandler).getDevicesOfUser(input);
        Mockito.verify(foodItemHandler).transferFoodItems(input, PrincipalFilterTest.TEST_USER.toUser(), devices, PrincipalFilterTest.TEST_USER.toDevice());
        Mockito.verify(userDbHandler).rollback();
    }

    @Test
    public void deleteSuccessfully() {
        List<Identifiable<UserDevice>> devices = new LinkedList<>();
        UserForDeletion input = new UserForDeletion(1, 2);
        devices.add(UserDeviceForGetting.builder()
                .id(1)
                .version(2)
                .name("fdsa")
                .belongsTo(input.id())
                .build());
        devices.add(UserDeviceForGetting.builder()
                .id(2)
                .version(2)
                .name("fdsa")
                .belongsTo(input.id())
                .build());
        devices.add(UserDeviceForGetting.builder()
                .id(3)
                .version(2)
                .name("fdsa")
                .belongsTo(input.id())
                .build());
        Mockito.when(deviceHandler.getDevicesOfUser(input)).thenReturn(Validation.success(devices));
        Mockito.when(foodItemHandler.transferFoodItems(input, PrincipalFilterTest.TEST_USER.toUser(), devices, PrincipalFilterTest.TEST_USER.toDevice()))
                .thenReturn(StatusCode.SUCCESS);
        Mockito.when(userDbHandler.delete(input)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(userDbHandler.commit()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(input);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(deviceHandler).getDevicesOfUser(input);
        Mockito.verify(foodItemHandler).transferFoodItems(input, PrincipalFilterTest.TEST_USER.toUser(), devices, PrincipalFilterTest.TEST_USER.toDevice());
        Mockito.verify(userDbHandler).delete(input);
        Mockito.verify(userDbHandler).commit();
    }
}
