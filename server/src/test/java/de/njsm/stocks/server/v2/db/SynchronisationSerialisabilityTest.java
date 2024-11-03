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

import de.njsm.stocks.common.api.Location;
import de.njsm.stocks.common.api.LocationForInsertion;
import de.njsm.stocks.common.api.StatusCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SynchronisationSerialisabilityTest extends DbTestCase {

    private LocationHandler writeHandler;

    private LocationHandler readHandler;

    private Connection writeConnection;

    private Connection readConnection;

    @Nested
    public final class OpenReadingConnectionFirst {

        @BeforeEach
        void startReadingBeforeWriting() throws Exception {
            createReadConnection();
            Thread.sleep(10);
            createWriteConnection();
            setupHandlers();
        }

        @Test
        void whenWritingFinishesFirstReadingIsAborted() {
            var addResult = writeHandler.addReturningId(LocationForInsertion.builder()
                    .name("name")
                    .description("description")
                    .build());
            assertTrue(addResult.isSuccess());

            var writeCommitResult = writeHandler.commit();
            assertEquals(StatusCode.SUCCESS, writeCommitResult);

            var readResult = readHandler.get(true, Instant.EPOCH);
            assertEquals(StatusCode.SERIALISATION_CONFLICT, readResult.fail());
        }

        @Test
        void whenReadingBeforeWritingFinishesReadingWaits() {
            var addResult = writeHandler.addReturningId(LocationForInsertion.builder()
                    .name("name")
                    .description("description")
                    .build());
            assertTrue(addResult.isSuccess());

            var readResult = readHandler.get(true, Instant.EPOCH);
            assertTrue(readResult.isFail());
            assertEquals(StatusCode.DATABASE_UNREACHABLE, readResult.fail());
        }

        @Test
        void whenReadingFinishesFirstTransactionsAreSerialized() {
            var readResult = readHandler.get(true, Instant.EPOCH);
            assertTrue(readResult.isSuccess());
            List<Location> locations = readResult.success().toList();
            assertEquals(2, locations.size());

            var readCommitResult = readHandler.commit();
            assertEquals(StatusCode.SUCCESS, readCommitResult);

            var addResult = writeHandler.addReturningId(LocationForInsertion.builder()
                    .name("name")
                    .description("description")
                    .build());
            assertTrue(addResult.isSuccess());

            var writeCommitResult = writeHandler.commit();
            assertEquals(StatusCode.SUCCESS, writeCommitResult);
        }

        @Test
        void whenWritingBeforeReadingFinishesWritingWaits() {
            var readResult = readHandler.get(true, Instant.EPOCH);
            assertTrue(readResult.isSuccess());
            List<Location> locations = readResult.success().toList();
            assertEquals(2, locations.size());

            var addResult = writeHandler.addReturningId(LocationForInsertion.builder()
                    .name("name")
                    .description("description")
                    .build());
            assertTrue(addResult.isFail());
            assertEquals(StatusCode.DATABASE_UNREACHABLE, addResult.fail());
        }
    }

    @Nested
    final class OpenWritingConnectionFirst {

        @BeforeEach
        void startWritingBeforeReading() throws Exception {
            createWriteConnection();
            Thread.sleep(10);
            createReadConnection();
            setupHandlers();
        }

        @Test
        void whenWritingFinishesFirstTransactionsAreSerialized() {
            var addResult = writeHandler.addReturningId(LocationForInsertion.builder()
                    .name("name")
                    .description("description")
                    .build());
            assertTrue(addResult.isSuccess());
            var idOfNewLocation = addResult.success();

            var writeCommitResult = writeHandler.commit();
            assertEquals(StatusCode.SUCCESS, writeCommitResult);

            var readResult = readHandler.get(true, Instant.EPOCH);
            assertTrue(readResult.isSuccess());
            List<Location> locations = readResult.success().toList();
            assertTrue(locations.stream().anyMatch(v -> v.id() == idOfNewLocation));

            var readCommitResult = readHandler.commit();
            assertEquals(StatusCode.SUCCESS, readCommitResult);
        }

        @Test
        void whenReadingBeforeWritingFinishesReadingWaits() {
            var addResult = writeHandler.addReturningId(LocationForInsertion.builder()
                    .name("name")
                    .description("description")
                    .build());
            assertTrue(addResult.isSuccess());

            var readResult = readHandler.get(true, Instant.EPOCH);
            assertTrue(readResult.isFail());
            assertEquals(StatusCode.DATABASE_UNREACHABLE, readResult.fail());
        }

        @Test
        void whenReadingFinishesFirstWritingIsAborted() {
            var readResult = readHandler.get(true, Instant.EPOCH);
            assertTrue(readResult.isSuccess());
            List<Location> locations = readResult.success().toList();
            assertEquals(2, locations.size());

            var readCommitResult = readHandler.commit();
            assertEquals(StatusCode.SUCCESS, readCommitResult);

            var addResult = writeHandler.addReturningId(LocationForInsertion.builder()
                    .name("name")
                    .description("description")
                    .build());
            assertEquals(StatusCode.SERIALISATION_CONFLICT, addResult.fail());
        }

        @Test
        void whenWritingBeforeReadingFinishesWritingWaits() {
            var readResult = readHandler.get(true, Instant.EPOCH);
            assertTrue(readResult.isSuccess());
            List<Location> locations = readResult.success().toList();
            assertEquals(2, locations.size());

            var addResult = writeHandler.addReturningId(LocationForInsertion.builder()
                    .name("name")
                    .description("description")
                    .build());
            assertTrue(addResult.isFail());
            assertEquals(StatusCode.DATABASE_UNREACHABLE, addResult.fail());
        }
    }

    private void createWriteConnection() throws SQLException {
        writeConnection = configureNewConnection();
    }

    private void createReadConnection() throws SQLException {
        readConnection = configureNewConnection();
    }

    private Connection configureNewConnection() throws SQLException {
        var result = DbTestCase.createConnection();
        result.setAutoCommit(false);
        result.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

        // To detect and assert waiting, set a low timeout on lock waiting
        // which triggers the application to return StatusCode.DATABASE_UNREACHABLE
        result.createStatement().execute("set lock_timeout = '100ms'");
        return result;
    }

    private void setupHandlers() {
        writeHandler = new LocationHandler(new MockConnectionFactory(writeConnection), null);
        writeHandler.setPrincipals(TEST_USER);
        readHandler = new LocationHandler(new MockConnectionFactory(readConnection), null);
        readHandler.setPrincipals(TEST_USER);
    }

    @AfterEach
    void closeConnections() throws java.sql.SQLException {
        if (!writeConnection.isClosed()) {
            writeConnection.rollback();
        }
        writeConnection.close();

        if (!readConnection.isClosed()) {
            readConnection.rollback();
        }
        readConnection.close();
    }

    private static final class MockConnectionFactory extends ConnectionFactory {

        private final Connection connection;

        public MockConnectionFactory(Connection connection) {
            super(null);
            this.connection = connection;
        }

        @Override
        public void initConnection() {
        }

        @Override
        public Connection getConnection() {
            return connection;
        }

        @Override
        public Optional<Connection> getExistingConnection() {
            return Optional.of(connection);
        }

        @Override
        public void reset() {
        }
    }
}
