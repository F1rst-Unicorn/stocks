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

import de.njsm.stocks.client.storage.DatabaseManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UpgradeManagerTest {

    private UpgradeManager uut;

    private DatabaseManager dbManager;

    @Before
    public void setup() throws Exception {
        dbManager = mock(DatabaseManager.class);
        uut = new UpgradeManager(dbManager, null);
    }

    @Test
    public void positiveUpgradeDetectionWorks() throws Exception {
        when(dbManager.getDbVersion()).thenReturn(Version.PRE_VERSIONED);

        assertTrue(uut.needsUpgrade());
    }

    @Test
    public void negativeUpgradeDetectionWorks() throws Exception {
        when(dbManager.getDbVersion()).thenReturn(Version.CURRENT);

        assertFalse(uut.needsUpgrade());
    }
}