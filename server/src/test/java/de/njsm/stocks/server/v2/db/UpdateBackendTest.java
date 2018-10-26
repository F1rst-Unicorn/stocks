package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.Config;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.Update;
import fj.data.Validation;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UpdateBackendTest extends DbTestCase {

    private UpdateBackend uut;

    @Before
    public void setup() {

        Config c = new Config(System.getProperties());
        uut = new UpdateBackend(getConnectionFactory(),
                getNewResourceIdentifier());
    }

    @Test
    public void getUpdates() {
        Validation<StatusCode, List<Update>> result = uut.getUpdates();

        assertTrue(result.isSuccess());
        assertEquals(6, result.success().size());
    }
}