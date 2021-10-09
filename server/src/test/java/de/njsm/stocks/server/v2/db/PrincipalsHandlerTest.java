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

import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.util.Principals;
import fj.data.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrincipalsHandlerTest extends DbTestCase {

    private PrincipalsHandler uut;

    @BeforeEach
    public void setup() {
        uut = new PrincipalsHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                CIRCUIT_BREAKER_TIMEOUT);
        uut.setPrincipals(TEST_USER);
    }

    @Test
    void fetchingJobRunnerPrincipalWorks() {
        Principals expected = new Principals("Stocks", "Job Runner", 2, 2);

        Validation<StatusCode, Principals> actual = uut.getJobRunnerPrincipal();

        assertTrue(actual.isSuccess());
        assertEquals(expected, actual.success());
    }

    @Test
    public void fetchPrincipals() {
        int numberOfDevices = new UserDeviceHandlerTest().getNumberOfEntities() - 1; // don't return pending device

        Validation<StatusCode, Set<Principals>> output = uut.getPrincipals();

        assertTrue(output.isSuccess());
        assertEquals(numberOfDevices, output.success().size());
        assertThat(output.success(), hasItem(new Principals("Default", "Default", 1, 1)));
        assertThat(output.success(), hasItem(new Principals("Bob", "mobile", 3, 3)));
        assertThat(output.success(), hasItem(new Principals("Bob", "mobile2", 3, 4)));
        assertThat(output.success(), hasItem(new Principals("Alice", "laptop", 4, 5)));
    }
}
