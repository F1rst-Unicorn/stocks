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

package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.server.v1.internal.data.Data;
import de.njsm.stocks.server.v1.internal.data.UpdateFactory;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class UpdateEndpointTest extends BaseTestEndpoint {

    private UpdateEndpoint uut;

    private DatabaseHandler handler;


    @Before
    public void setup() {
        handler = Mockito.mock(DatabaseHandler.class);
        uut = new UpdateEndpoint(handler);

        Mockito.when(handler.get(UpdateFactory.f))
                .thenReturn(new Data[0]);
    }
    
    @Test
    public void testGettingUpdates() {
        Data[] result = uut.getUpdates(createMockRequest());

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length);
        Mockito.verify(handler).get(UpdateFactory.f);
        Mockito.verifyNoMoreInteractions(handler);
    }

}
