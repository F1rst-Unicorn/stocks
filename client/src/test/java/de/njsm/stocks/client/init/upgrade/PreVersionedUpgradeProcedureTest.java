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

package de.njsm.stocks.client.init.upgrade;

import de.njsm.stocks.client.storage.DatabaseHelper;
import de.njsm.stocks.client.storage.DatabaseManager;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PreVersionedUpgradeProcedureTest {

    private static DatabaseHelper helper;

    private PreVersionedUpgradeProcedure uut;

    private DatabaseManager dbManager;

    @BeforeClass
    public static void setupClass() throws Exception {
        helper = new DatabaseHelper();
        helper.setupDatabase();
    }

    @Before
    public void setup() throws Exception {
        dbManager = new DatabaseManager();
        uut = new PreVersionedUpgradeProcedure(dbManager);
        helper.fillData();
        helper.runSqlCommand("DROP TABLE Config");
    }

    @AfterClass
    public static void tearDown() throws Exception {
        helper.removeDatabase();
    }

    @Test
    public void runUpgrade() throws Exception {

        uut.upgrade();

        assertEquals(Version.V_0_5_0, dbManager.getDbVersion());
    }
}