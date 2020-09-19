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

import de.njsm.stocks.server.v2.business.data.Update;
import de.njsm.stocks.server.v2.db.UpdateBackend;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.container.AsyncResponse;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertTrue;

public class UpdateManagerTest {

    private UpdateManager uut;

    private UpdateBackend backend;

    @Before
    public void setup() {
        backend = Mockito.mock(UpdateBackend.class);
        uut = new UpdateManager(backend);
    }

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(backend);
    }

    @Test
    public void gettingUpdatesWorks() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);
        Mockito.when(backend.get()).thenReturn(Validation.success(Stream.empty()));
        Mockito.when(backend.commit()).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, Stream<Update>> result = uut.getUpdates(r);

        assertTrue(result.isSuccess());
        Mockito.verify(backend).get();
        Mockito.verify(backend).setReadOnly();
    }

}
