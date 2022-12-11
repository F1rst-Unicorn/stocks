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

import androidx.room.Room;
import androidx.room.testing.MigrationTestHelper;
import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.business.entities.EntityType;
import de.njsm.stocks.client.database.StocksDatabase;
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.time.Instant;

import static org.junit.Assert.assertEquals;

public class Legacy40To44Test {

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
        try (var db = helper.createDatabase(TEST_DB, 40)) {
            db.execSQL("insert into updates (_id, name, last_update) values (1, 'scaled_unit', '1970-01-01 00:00:00.000000')");
            db.execSQL("insert into updates (_id, name, last_update) values (2, 'Location', '1970-01-01 00:00:00.000000')");
            db.execSQL("insert into updates (_id, name, last_update) values (3, 'Food', '1970-01-01 00:00:00.000000')");
            db.execSQL("insert into updates (_id, name, last_update) values (4, 'EAN_number', '1970-01-01 00:00:00.000000')");
            db.execSQL("insert into updates (_id, name, last_update) values (5, 'User_device', '1970-01-01 00:00:00.000000')");
            db.execSQL("insert into updates (_id, name, last_update) values (6, 'recipe_product', '1970-01-01 00:00:00.000000')");
            db.execSQL("insert into updates (_id, name, last_update) values (7, 'recipe_ingredient', '1970-01-01 00:00:00.000000')");
            db.execSQL("insert into updates (_id, name, last_update) values (8, 'unit', '1970-01-01 00:00:00.000000')");
            db.execSQL("insert into updates (_id, name, last_update) values (9, 'User', '1970-01-01 00:00:00.000000')");
            db.execSQL("insert into updates (_id, name, last_update) values (10, 'Food_item', '1970-01-01 00:00:00.000000')");
            db.execSQL("insert into updates (_id, name, last_update) values (11, 'recipe', '1970-01-01 00:00:00.000000')");
        }
        try (var db = helper.runMigrationsAndValidate(TEST_DB, 44, true, new Legacy40To44())) {
            StocksDatabase orm = Room.databaseBuilder(InstrumentationRegistry.getInstrumentation().getTargetContext(),
                            StocksDatabase.class,
                            TEST_DB)
                    .openHelperFactory(new RequerySQLiteOpenHelperFactory())
                    .build();

            assertEquals(Instant.EPOCH, orm.errorDao().getTransactionTimeOf(EntityType.SCALED_UNIT));
            assertEquals(Instant.EPOCH, orm.errorDao().getTransactionTimeOf(EntityType.LOCATION));
            assertEquals(Instant.EPOCH, orm.errorDao().getTransactionTimeOf(EntityType.FOOD));
            assertEquals(Instant.EPOCH, orm.errorDao().getTransactionTimeOf(EntityType.EAN_NUMBER));
            assertEquals(Instant.EPOCH, orm.errorDao().getTransactionTimeOf(EntityType.USER_DEVICE));
            assertEquals(Instant.EPOCH, orm.errorDao().getTransactionTimeOf(EntityType.RECIPE_PRODUCT));
            assertEquals(Instant.EPOCH, orm.errorDao().getTransactionTimeOf(EntityType.RECIPE_INGREDIENT));
            assertEquals(Instant.EPOCH, orm.errorDao().getTransactionTimeOf(EntityType.UNIT));
            assertEquals(Instant.EPOCH, orm.errorDao().getTransactionTimeOf(EntityType.USER));
            assertEquals(Instant.EPOCH, orm.errorDao().getTransactionTimeOf(EntityType.FOOD_ITEM));
            assertEquals(Instant.EPOCH, orm.errorDao().getTransactionTimeOf(EntityType.RECIPE));
        }
    }
}
