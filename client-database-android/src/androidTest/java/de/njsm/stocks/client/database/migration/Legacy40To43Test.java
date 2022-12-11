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

public class Legacy40To43Test {

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
        try (var db = helper.createDatabase(TEST_DB, 40)) {}
        try (var db = helper.runMigrationsAndValidate(TEST_DB, 43, true, new Legacy40To43())) {}
    }

}
