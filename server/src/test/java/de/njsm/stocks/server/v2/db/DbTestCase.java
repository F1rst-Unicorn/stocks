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

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

public abstract class DbTestCase {

    private static int resourceCounter = 0;

    private static ConnectionFactory factory;

    private static Connection connection;

    public static final int CIRCUIT_BREAKER_TIMEOUT = 5000;

    private static ComboPooledDataSource ds;

    @BeforeAll
    public static void beforeClass() {
        ds = new ComboPooledDataSource();
        ds.setJdbcUrl(getUrl());
        ds.setProperties(getPostgresqlProperties(System.getProperties()));
        ds.setMaxPoolSize(1);
        ds.setMinPoolSize(1);
    }

    @AfterAll
    public static void afterClass() {
        ds.close();
    }

    @BeforeEach
    public void resetDatabase() throws SQLException {
        factory = new ConnectionFactory(ds);
        connection = factory.getConnection();
        getSampleData(connection).apply();

        resourceCounter++;
    }

    SampleData getSampleData(Connection connection) {
        return new SampleData(connection);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        connection.close();
    }

    protected ConnectionFactory getConnectionFactory() {
        return factory;
    }

    protected DSLContext getDSLContext() {
        return DSL.using(connection, SQLDialect.POSTGRES);
    }

    protected String getNewResourceIdentifier() {
        return "hystrix group " + resourceCounter;
    }

    static Connection createConnection() throws SQLException {
        String url = getUrl();

        return DriverManager.getConnection(url, getPostgresqlProperties(System.getProperties()));
    }

    protected static String getUrl() {
        String address = System.getProperty("de.njsm.stocks.server.v2.db.host");
        String port = System.getProperty("de.njsm.stocks.server.v2.db.port");
        String name = System.getProperty("de.njsm.stocks.server.v2.db.name");

        return String.format("jdbc:postgresql://%s:%s/%s",
                address,
                port,
                name);
    }

    protected static Properties getPostgresqlProperties(Properties config) {
        String postgresqlConfigPrefix = "de.njsm.stocks.server.v2.db.postgres.";
        Properties result = new Properties();

        for (Map.Entry<Object, Object> entry: config.entrySet()) {
            if (entry.getKey() instanceof String &&
                entry.getValue() instanceof String) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                if (key.startsWith(postgresqlConfigPrefix) &&
                        ! value.isEmpty()) {
                    result.put(
                            key.replace(postgresqlConfigPrefix, ""),
                            entry.getValue()
                    );
                }
            }
        }
        return result;
    }


}
