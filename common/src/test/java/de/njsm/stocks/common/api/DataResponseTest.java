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

package de.njsm.stocks.common.api;

import fj.data.Validation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DataResponseTest {

    @Test
    public void testSuccess() {
        String data = "hello";
        DataResponse<String> uut = new DataResponse<>(Validation.success(data));

        assertEquals(data, uut.data);
        assertEquals(StatusCode.SUCCESS, uut.status);
    }

    @Test
    public void testFailure() {
        StatusCode error = StatusCode.GENERAL_ERROR;
        DataResponse<String> uut = new DataResponse<>(Validation.fail(error));

        assertNull(uut.data);
        assertEquals(error, uut.status);
    }
}
