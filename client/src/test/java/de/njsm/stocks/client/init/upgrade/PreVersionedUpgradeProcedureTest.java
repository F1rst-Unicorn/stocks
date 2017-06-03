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