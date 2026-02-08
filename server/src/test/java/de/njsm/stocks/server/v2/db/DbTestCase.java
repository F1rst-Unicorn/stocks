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

import com.zaxxer.hikari.HikariDataSource;
import de.njsm.stocks.server.v2.web.security.StocksAuthentication;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import static de.njsm.stocks.server.v2.web.security.HeaderAuthenticatorTest.TEST_USER;

public abstract class DbTestCase {

    private static ConnectionFactory factory;

    private static Connection connection;

    private static HikariDataSource ds;

    @BeforeAll
    public static void beforeClass() throws SQLException {
        var postgresql = new PGSimpleDataSource();
        postgresql.setURL(getUrl());
        Properties postgresqlProperties = getPostgresqlProperties(System.getProperties());
        for (Object key : postgresqlProperties.keySet()) {
            postgresql.setProperty((String) key, postgresqlProperties.getProperty((String) key));
        }
        ds = new HikariDataSource();
        ds.setDataSource(postgresql);
        ds.setMinimumIdle(1);
        ds.setMaximumPoolSize(1);
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
        SecurityContextHolder.getContext().setAuthentication(new StocksAuthentication(TEST_USER));
    }

    protected SampleData getSampleData(Connection connection) {
        return new SampleData(connection);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        if (!connection.isClosed()) {
            connection.commit();
        }
        connection.close();
    }

    protected ConnectionFactory getConnectionFactory() {
        return factory;
    }

    protected ConnectionFactory getUnreachableConnectionFactory() throws SQLException {
        var postgresql = new PGSimpleDataSource();
        postgresql.setURL(getUrlByHost("unreachable.example"));
        Properties postgresqlProperties = getPostgresqlProperties(System.getProperties());
        for (Object key : postgresqlProperties.keySet()) {
            postgresql.setProperty((String) key, postgresqlProperties.getProperty((String) key));
        }
        var ds = new HikariDataSource();
        ds.setDataSource(postgresql);
        ds.setMinimumIdle(1);
        ds.setMaximumPoolSize(1);

        return new ConnectionFactory(ds);
    }

    protected DSLContext getDSLContext() {
        var settings = new Settings().withReturnAllOnUpdatableRecord(true);
        return DSL.using(connection, SQLDialect.POSTGRES, settings);
    }

    static Connection createConnection() throws SQLException {
        String url = getUrl();

        return DriverManager.getConnection(url, getPostgresqlProperties(System.getProperties()));
    }

    protected static String getUrl() {
        return getUrlByHost(System.getProperty("de.njsm.stocks.server.v2.db.host"));
    }

    private static String getUrlByHost(String host) {
        String port = System.getProperty("de.njsm.stocks.server.v2.db.port");
        String name = System.getProperty("de.njsm.stocks.server.v2.db.name");

        return String.format("jdbc:postgresql://%s:%s/%s",
                host,
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
