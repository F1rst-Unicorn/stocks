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

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.EanNumber;
import de.njsm.stocks.server.v2.business.data.Food;
import fj.data.Validation;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.junit.Assert.*;

public class EanNumberHandlerTest extends DbTestCase {

    private EanNumberHandler uut;

    @Before
    public void setup() {
        uut = new EanNumberHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                CIRCUIT_BREAKER_TIMEOUT,
                new InsertVisitor<>());
        uut.setPrincipals(TEST_USER);
    }

    @Test
    public void addAEanNumber() {
        EanNumber data = new EanNumber(1, 1, "Code", 1);

        Validation<StatusCode, Integer> code = uut.add(data);

        assertTrue(code.isSuccess());

        Validation<StatusCode, Stream<EanNumber>> dbData = uut.get(false, Instant.EPOCH);

        assertTrue(dbData.isSuccess());

        assertTrue(dbData.success().map(f -> f.eanCode).anyMatch(name -> name.equals(data.eanCode)));
    }

    @Test
    public void bitemporalDataIsPresentWhenDesired() {

        Validation<StatusCode, Stream<EanNumber>> result = uut.get(true, Instant.EPOCH);

        EanNumber sample = result.success().findAny().get();
        assertNotNull(sample.validTimeStart);
        assertNotNull(sample.validTimeEnd);
        assertNotNull(sample.transactionTimeStart);
        assertNotNull(sample.transactionTimeEnd);
    }

    @Test
    public void deleteAEanNumber() {
        EanNumber data = new EanNumber(1, 0, "EAN BEER", 1);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.SUCCESS, result);

        Validation<StatusCode, Stream<EanNumber>> dbData = uut.get(false, Instant.EPOCH);

        assertTrue(dbData.isSuccess());

        assertEquals(0, dbData.success().count());
    }

    @Test
    public void invalidDataVersionIsRejected() {
        EanNumber data = new EanNumber(1, 1, "EAN BEER", 1);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);

        Validation<StatusCode, Stream<EanNumber>> dbData = uut.get(false, Instant.EPOCH);

        assertTrue(dbData.isSuccess());

        assertEquals(1, dbData.success().count());
    }

    @Test
    public void unknownDeletionsAreReported() {
        EanNumber data = new EanNumber(100, 1, "EAN BEER", 1);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void deletingCodesWithoutFoodIsOk() {

        StatusCode result = uut.deleteOwnedByFood(new Food(1, 1));

        assertEquals(StatusCode.SUCCESS, result);
    }

    @Test
    public void deletingCodesWorks() {
        long codes = uut.get(false, Instant.EPOCH).success().count();
        assertEquals(1, codes);

        StatusCode result = uut.deleteOwnedByFood(new Food(2, 1));

        assertEquals(StatusCode.SUCCESS, result);
        codes = uut.get(false, Instant.EPOCH).success().count();
        assertEquals(0, codes);
    }
}
