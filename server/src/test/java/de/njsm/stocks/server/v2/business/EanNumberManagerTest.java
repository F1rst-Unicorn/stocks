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

package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.common.api.EanNumber;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.EanNumberForDeletion;
import de.njsm.stocks.common.api.EanNumberForInsertion;
import de.njsm.stocks.server.v2.db.EanNumberHandler;
import fj.data.Validation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.ws.rs.container.AsyncResponse;
import java.time.Instant;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class EanNumberManagerTest {

    private EanNumberManager uut;

    private EanNumberHandler backend;

    @BeforeEach
    public void setup() {
        backend = Mockito.mock(EanNumberHandler.class);
        Mockito.when(backend.commit()).thenReturn(StatusCode.SUCCESS);
        uut = new EanNumberManager(backend);
        uut.setPrincipals(TEST_USER);
    }

    @AfterEach
    public void tearDown() {
        Mockito.verify(backend).setPrincipals(TEST_USER);
        Mockito.verifyNoMoreInteractions(backend);
    }

    @Test
    public void gettingItemsIsForwarded() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);
        Mockito.when(backend.get(Instant.EPOCH, Instant.EPOCH)).thenReturn(Validation.success(Stream.empty()));
        when(backend.setReadOnly()).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, Stream<EanNumber>> result = uut.get(r, Instant.EPOCH, Instant.EPOCH);

        assertTrue(result.isSuccess());
        Mockito.verify(backend).get(Instant.EPOCH, Instant.EPOCH);
        Mockito.verify(backend).setReadOnly();
    }

    @Test
    public void testAddingItem() {
        EanNumberForInsertion data = EanNumberForInsertion.builder()
                .eanNumber("code")
                .identifiesFood(2)
                .build();
        Mockito.when(backend.add(data)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.add(data);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(backend).add(data);
        Mockito.verify(backend).commit();
    }

    @Test
    public void testDeletingItem() {
        EanNumberForDeletion data = EanNumberForDeletion.builder()
                .id(1)
                .version(2)
                .build();
        Mockito.when(backend.delete(data)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(backend).delete(data);
        Mockito.verify(backend).commit();
    }
}
