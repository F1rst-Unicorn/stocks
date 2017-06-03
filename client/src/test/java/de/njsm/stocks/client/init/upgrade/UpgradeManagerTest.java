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