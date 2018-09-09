package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.Config;
import de.njsm.stocks.server.v1.internal.db.DatabaseHelper;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.Update;
import fj.data.Validation;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UpdateBackendTest {

    private UpdateBackend uut;

    private static int resourceCounter = 0;

    @Before
    public void resetDatabase() throws SQLException {
        DatabaseHelper.resetSampleData();

        Config c = new Config(System.getProperties());
        uut = new UpdateBackend(String.format("jdbc:mariadb://%s:%s/%s?useLegacyDatetimeCode=false&serverTimezone=+00:00",
                c.getDbAddress(), c.getDbPort(), c.getDbName()),
                c.getDbUsername(),
                c.getDbPassword(),
                "hystrix group update" + String.valueOf(resourceCounter));
        resourceCounter++;
    }

    @Test
    public void getUpdates() {
        Validation<StatusCode, List<Update>> result = uut.getUpdates();

        assertTrue(result.isSuccess());
        assertEquals(6, result.success().size());
    }
}