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
import fj.data.Validation;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EanNumberHandlerTest extends DbTestCase {

    private EanNumberHandler uut;

    @Before
    public void setup() {
        uut = new EanNumberHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                CIRCUIT_BREAKER_TIMEOUT,
                new InsertVisitor<>());
    }

    @Test
    public void addAEanNumber() {
        EanNumber data = new EanNumber(1, 1, "Code", 1);

        Validation<StatusCode, Integer> code = uut.add(data);

        assertTrue(code.isSuccess());

        Validation<StatusCode, Stream<EanNumber>> dbData = uut.get(false);

        assertTrue(dbData.isSuccess());

        assertTrue(dbData.success().map(f -> f.eanCode).anyMatch(name -> name.equals(data.eanCode)));
    }

    @Test
    public void deleteAEanNumber() {
        EanNumber data = new EanNumber(1, 0, "EAN BEER", 1);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.SUCCESS, result);

        Validation<StatusCode, Stream<EanNumber>> dbData = uut.get(false);

        assertTrue(dbData.isSuccess());

        assertEquals(0, dbData.success().count());
    }

    @Test
    public void invalidDataVersionIsRejected() {
        EanNumber data = new EanNumber(1, 1, "EAN BEER", 1);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);

        Validation<StatusCode, Stream<EanNumber>> dbData = uut.get(false);

        assertTrue(dbData.isSuccess());

        assertEquals(1, dbData.success().count());
    }

    @Test
    public void unknownDeletionsAreReported() {
        EanNumber data = new EanNumber(100, 1, "EAN BEER", 1);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.NOT_FOUND, result);
    }
}
