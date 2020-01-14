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

package de.njsm.stocks.server.v2.business.data;

import de.njsm.stocks.server.v2.business.StatusCode;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class HealthTest {

    @Test
    public void allFailingMapsToGeneralError() {
        Health uut = new Health(false, false);
        assertFalse(uut.toValidation().isSuccess());
        assertEquals(StatusCode.GENERAL_ERROR, uut.toValidation().fail());
    }

    @Test
    public void failingDbMapsToDbError() {
        Health uut = new Health(false, true);
        assertFalse(uut.toValidation().isSuccess());
        assertEquals(StatusCode.DATABASE_UNREACHABLE, uut.toValidation().fail());
    }

    @Test
    public void failingCaMapsToCaError() {
        Health uut = new Health(true, false);
        assertFalse(uut.toValidation().isSuccess());
        assertEquals(StatusCode.CA_UNREACHABLE, uut.toValidation().fail());
    }

    @Test
    public void successfulIsReported() {
        Health uut = new Health(true, true);
        assertTrue(uut.toValidation().isSuccess());
        assertEquals(uut, uut.toValidation().success());
    }
}