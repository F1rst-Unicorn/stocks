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
import fj.data.Validation;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;

public class ConnectionHandlerTest extends DbTestCase {

    private ConnectionHandler uut;

    @Before
    public void setup() {
        uut = new ConnectionHandler(getNewResourceIdentifier(),
                getConnectionFactory(),
                CIRCUIT_BREAKER_TIMEOUT);
    }

    @Test
    public void defaultErrorCodeIsCorrect() {
        assertEquals(StatusCode.DATABASE_UNREACHABLE, uut.getDefaultErrorCode());
    }

    @Test
    public void otherSqlExceptionIsForwarded() {
        StatusCode result = uut.runCommand(c -> {
            throw new SQLException();
        });

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
    }

    @Test
    public void nestedSqlExceptionIsForwarded() {
        StatusCode result = uut.runCommand(c -> {
            throw new RuntimeException("", new SQLException("", "40001", null));
        });

        assertEquals(StatusCode.SERIALISATION_CONFLICT, result);
    }

    @Test
    public void otherExceptionIsForwarded() {
        StatusCode result = uut.runCommand(c -> {
            throw new RuntimeException();
        });

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
    }

    @Test
    public void sqlExceptionWithDifferentCodeIsIgnored() {
        assertFalse(ConnectionHandler.isSerialisationConflict(new SQLException("", "40002", null)));
    }

    @Test
    public void serialisationExceptionIsTransformed() {
        RuntimeException e = new RuntimeException("", new SQLException("", "40001", null));

        Validation<StatusCode, Object> result = ConnectionHandler.lookForSqlException(e);

        assertEquals(StatusCode.SERIALISATION_CONFLICT, result.fail());
    }

    @Test(expected = RuntimeException.class)
    public void otherExceptionIsThrown() {
        RuntimeException e = new RuntimeException("", new SQLException("", "40002", null));

        ConnectionHandler.lookForSqlException(e);
    }
}
