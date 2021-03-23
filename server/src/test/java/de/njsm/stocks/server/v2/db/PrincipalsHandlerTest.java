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

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.StatusCode;
import fj.data.Validation;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PrincipalsHandlerTest extends DbTestCase {

    private PrincipalsHandler uut;

    @Before
    public void setup() {
        uut = new PrincipalsHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                CIRCUIT_BREAKER_TIMEOUT);
        uut.setPrincipals(TEST_USER);
    }

    @Test
    public void fetchPrincipals() {

        Validation<StatusCode, Set<Principals>> output = uut.getPrincipals();

        assertTrue(output.isSuccess());
        assertEquals(4, output.success().size());
        assertTrue(output.success().contains(new Principals("Default", "Default", 1, 1)));
        assertTrue(output.success().contains(new Principals("Bob", "mobile", 2, 2)));
        assertTrue(output.success().contains(new Principals("Bob", "mobile2", 2, 3)));
        assertTrue(output.success().contains(new Principals("Alice", "laptop", 3, 4)));
    }
}
