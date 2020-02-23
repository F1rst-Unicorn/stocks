package de.njsm.stocks.server.v2.web.data;

import de.njsm.stocks.server.v2.business.StatusCode;
import fj.data.Validation;
import org.junit.Test;

public class ListResponseTest {

    @Test
    public void dataIsSet() {
        ListResponse<Object> r = new ListResponse<>(Validation.fail(StatusCode.DATABASE_UNREACHABLE));
    }
}