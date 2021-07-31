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
import org.junit.Test;

import javax.ws.rs.core.Response;

import static junit.framework.TestCase.assertEquals;

public class StatusCodeTest {

    @Test(expected = RuntimeException.class)
    public void invalidValidationWithFailIsRaised() {
        StatusCode.toCode(Validation.fail(StatusCode.SUCCESS));
    }

    @Test(expected = RuntimeException.class)
    public void invalidValidationWithSuccessIsRaised() {
        StatusCode.toCode(Validation.success(StatusCode.DATABASE_UNREACHABLE));
    }

    @Test
    public void checkHttpCodeMappings() {
        assertEquals(Response.Status.OK, StatusCode.SUCCESS.toHttpStatus());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, StatusCode.GENERAL_ERROR.toHttpStatus());
        assertEquals(Response.Status.NOT_FOUND, StatusCode.NOT_FOUND.toHttpStatus());
        assertEquals(Response.Status.BAD_REQUEST, StatusCode.INVALID_DATA_VERSION.toHttpStatus());
        assertEquals(Response.Status.BAD_REQUEST, StatusCode.FOREIGN_KEY_CONSTRAINT_VIOLATION.toHttpStatus());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, StatusCode.DATABASE_UNREACHABLE.toHttpStatus());
        assertEquals(Response.Status.UNAUTHORIZED, StatusCode.ACCESS_DENIED.toHttpStatus());
        assertEquals(Response.Status.BAD_REQUEST, StatusCode.INVALID_ARGUMENT.toHttpStatus());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, StatusCode.CA_UNREACHABLE.toHttpStatus());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, StatusCode.SERIALISATION_CONFLICT.toHttpStatus());
    }
}
