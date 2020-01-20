package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.StatusCode;
import fj.data.Validation;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class PrincipalsHandlerTest extends DbTestCase {

    private PrincipalsHandler uut;

    @Before
    public void setup() {
        uut = new PrincipalsHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                CIRCUIT_BREAKER_TIMEOUT);
    }

    @Test
    public void fetchPrincipals() {

        Validation<StatusCode, Set<Principals>> output = uut.getPrincipals();

        assertTrue(output.isSuccess());
        assertEquals(3, output.success().size());
        assertTrue(output.success().contains(new Principals("Bob", "mobile", 1, 1)));
        assertTrue(output.success().contains(new Principals("Bob", "mobile2", 1, 2)));
        assertTrue(output.success().contains(new Principals("Alice", "laptop", 2, 3)));
    }
}