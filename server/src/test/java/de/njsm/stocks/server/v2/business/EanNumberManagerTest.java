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

import de.njsm.stocks.common.api.EanNumber;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.EanNumberForDeletion;
import de.njsm.stocks.common.api.EanNumberForInsertion;
import de.njsm.stocks.server.v2.db.EanNumberHandler;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.container.AsyncResponse;
import java.time.Instant;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class EanNumberManagerTest {

    private EanNumberManager uut;

    private EanNumberHandler backend;

    @Before
    public void setup() {
        backend = Mockito.mock(EanNumberHandler.class);
        Mockito.when(backend.commit()).thenReturn(StatusCode.SUCCESS);
        uut = new EanNumberManager(backend);
        uut.setPrincipals(TEST_USER);
    }

    @After
    public void tearDown() {
        Mockito.verify(backend).setPrincipals(TEST_USER);
        Mockito.verifyNoMoreInteractions(backend);
    }

    @Test
    public void gettingItemsIsForwarded() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);
        Mockito.when(backend.get(false, Instant.EPOCH)).thenReturn(Validation.success(Stream.empty()));

        Validation<StatusCode, Stream<EanNumber>> result = uut.get(r, false, Instant.EPOCH);

        assertTrue(result.isSuccess());
        Mockito.verify(backend).get(false, Instant.EPOCH);
        Mockito.verify(backend).setReadOnly();
    }

    @Test
    public void testAddingItem() {
        EanNumberForInsertion data = new EanNumberForInsertion(2, "code");
        Mockito.when(backend.add(data)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.add(data);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(backend).add(data);
        Mockito.verify(backend).commit();
    }

    @Test
    public void testDeletingItem() {
        EanNumberForDeletion data = new EanNumberForDeletion(1, 2);
        Mockito.when(backend.delete(data)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(backend).delete(data);
        Mockito.verify(backend).commit();
    }
}
