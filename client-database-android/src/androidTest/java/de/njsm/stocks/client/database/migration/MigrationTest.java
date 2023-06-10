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

package de.njsm.stocks.client.database.migration;

import androidx.room.testing.MigrationTestHelper;
import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.database.StocksDatabase;
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

public class MigrationTest {

    private static final String TEST_DB = "migration-test";

    @Rule
    public MigrationTestHelper helper;

    @Before
    public void setup() {
        helper = new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
                StocksDatabase.class.getCanonicalName(),
                new RequerySQLiteOpenHelperFactory());
    }

    @Test
    public void migratingFromLegacyToRewriteWorks() throws IOException {
        try (var __ = helper.createDatabase(TEST_DB, 46)) {}
        try (var __ = helper.runMigrationsAndValidate(TEST_DB, 47, true, new Migration46To47())) {}
    }

    @Test
    public void migratingTo48Works() throws IOException {
        try (var __ = helper.createDatabase(TEST_DB, 47)) {}
        try (var __ = helper.runMigrationsAndValidate(TEST_DB, 48, true, new Migration47To48())) {}
    }

    @Test
    public void migratingTo49Works() throws IOException {
        try (var __ = helper.createDatabase(TEST_DB, 48)) {}
        try (var __ = helper.runMigrationsAndValidate(TEST_DB, 49, true, new Migration48To49())) {}
    }

    @Test
    public void migratingTo50Works() throws IOException {
        try (var __ = helper.createDatabase(TEST_DB, 49)) {}
        try (var __ = helper.runMigrationsAndValidate(TEST_DB, 50, true, new Migration49To50())) {}
    }
}
