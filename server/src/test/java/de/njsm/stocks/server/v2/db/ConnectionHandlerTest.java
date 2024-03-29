/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.api.EanNumberForDeletion;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.v2.business.EanNumberManager;
import fj.data.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.junit.jupiter.api.Assertions.*;

public class ConnectionHandlerTest extends DbTestCase {

    private ConnectionHandler uut;

    @BeforeEach
    public void setup() {
        uut = new ConnectionHandler(getConnectionFactory());
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

    @Test
    public void otherExceptionIsThrown() {
        RuntimeException e = new RuntimeException("", new SQLException("", "40002", null));

        assertThrows(e.getClass(), () -> ConnectionHandler.lookForSqlException(e));
    }

    @Test
    public void failingDisablingAutoCommitOnRollbackResetsFactoryAnyway() throws SQLException {
        var connection = Mockito.mock(Connection.class);
        var connectionFactory = Mockito.mock(ConnectionFactory.class);
        Mockito.when(connectionFactory.getExistingConnection()).thenReturn(Optional.of(connection));
        Mockito.doThrow(new SQLException("test")).when(connection).setAutoCommit(false);
        uut = new ConnectionHandler(connectionFactory);

        uut.rollback();

        Mockito.verify(connectionFactory).getExistingConnection();
        Mockito.verify(connectionFactory).reset();
        Mockito.verify(connection).setAutoCommit(false);
        Mockito.verifyNoMoreInteractions(connection);
        Mockito.verifyNoMoreInteractions(connectionFactory);
    }

    @Test
    public void failingRollbackResetsFactoryAnyway() throws SQLException {
        var connection = Mockito.mock(Connection.class);
        var connectionFactory = Mockito.mock(ConnectionFactory.class);
        Mockito.when(connectionFactory.getExistingConnection()).thenReturn(Optional.of(connection));
        Mockito.doThrow(new SQLException("test")).when(connection).rollback();
        uut = new ConnectionHandler(connectionFactory);

        uut.rollback();

        Mockito.verify(connectionFactory).getExistingConnection();
        Mockito.verify(connectionFactory).reset();
        Mockito.verify(connection).setAutoCommit(false);
        Mockito.verify(connection).rollback();
        Mockito.verifyNoMoreInteractions(connection);
        Mockito.verifyNoMoreInteractions(connectionFactory);
    }

    @Test
    public void failingClosingAfterRollbackResetsFactoryAnyway() throws SQLException {
        var connection = Mockito.mock(Connection.class);
        var connectionFactory = Mockito.mock(ConnectionFactory.class);
        Mockito.when(connectionFactory.getExistingConnection()).thenReturn(Optional.of(connection));
        Mockito.doThrow(new SQLException("test")).when(connection).close();
        uut = new ConnectionHandler(connectionFactory);

        uut.rollback();

        Mockito.verify(connectionFactory).getExistingConnection();
        Mockito.verify(connectionFactory).reset();
        Mockito.verify(connection).setAutoCommit(false);
        Mockito.verify(connection).rollback();
        Mockito.verify(connection).close();
        Mockito.verifyNoMoreInteractions(connection);
        Mockito.verifyNoMoreInteractions(connectionFactory);
    }

    @Test
    public void failingDisablingAutoCommitOnCommitResetsFactoryAnyway() throws SQLException {
        var connection = Mockito.mock(Connection.class);
        var connectionFactory = Mockito.mock(ConnectionFactory.class);
        Mockito.when(connectionFactory.getExistingConnection()).thenReturn(Optional.of(connection));
        Mockito.doThrow(new SQLException("test")).when(connection).setAutoCommit(false);
        uut = new ConnectionHandler(connectionFactory);

        uut.commit();

        Mockito.verify(connectionFactory).getExistingConnection();
        Mockito.verify(connectionFactory).reset();
        Mockito.verify(connection).setAutoCommit(false);
        Mockito.verifyNoMoreInteractions(connection);
        Mockito.verifyNoMoreInteractions(connectionFactory);
    }

    @Test
    public void failingCommitResetsFactoryAnyway() throws SQLException {
        var connection = Mockito.mock(Connection.class);
        var connectionFactory = Mockito.mock(ConnectionFactory.class);
        Mockito.when(connectionFactory.getExistingConnection()).thenReturn(Optional.of(connection));
        Mockito.doThrow(new SQLException("test")).when(connection).commit();
        uut = new ConnectionHandler(connectionFactory);

        uut.commit();

        Mockito.verify(connectionFactory).getExistingConnection();
        Mockito.verify(connectionFactory).reset();
        Mockito.verify(connection).setAutoCommit(false);
        Mockito.verify(connection).commit();
        Mockito.verifyNoMoreInteractions(connection);
        Mockito.verifyNoMoreInteractions(connectionFactory);
    }

    @Test
    public void failingClosingAfterCommitResetsFactoryAnyway() throws SQLException {
        var connection = Mockito.mock(Connection.class);
        var connectionFactory = Mockito.mock(ConnectionFactory.class);
        Mockito.when(connectionFactory.getExistingConnection()).thenReturn(Optional.of(connection));
        Mockito.doThrow(new SQLException("test")).when(connection).close();
        uut = new ConnectionHandler(connectionFactory);

        uut.commit();

        Mockito.verify(connectionFactory).getExistingConnection();
        Mockito.verify(connectionFactory).reset();
        Mockito.verify(connection).setAutoCommit(false);
        Mockito.verify(connection).commit();
        Mockito.verify(connection).close();
        Mockito.verifyNoMoreInteractions(connection);
        Mockito.verifyNoMoreInteractions(connectionFactory);
    }

    @Test
    void unreachableDatabaseIsPropagatedByBusiness() throws SQLException {
        EanNumberManager business = new EanNumberManager(new EanNumberHandler(getUnreachableConnectionFactory()));
        business.setPrincipals(TEST_USER);

        var result = business.delete(EanNumberForDeletion.builder().id(1).version(1).build());

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
    }
}
