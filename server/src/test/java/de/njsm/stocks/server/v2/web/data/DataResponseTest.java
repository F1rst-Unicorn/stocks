package de.njsm.stocks.server.v2.web.data;

import de.njsm.stocks.server.v2.business.StatusCode;
import fj.data.Validation;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DataResponseTest {

    @Test
    public void testSuccess() {
        String data = "hello";
        DataResponse<String> uut = new DataResponse<>(Validation.success(data));

        assertEquals(data, uut.data);
        assertEquals(StatusCode.SUCCESS, uut.status);
    }

    @Test
    public void testFailure() {
        StatusCode error = StatusCode.GENERAL_ERROR;
        DataResponse<String> uut = new DataResponse<>(Validation.fail(error));

        assertNull(uut.data);
        assertEquals(error, uut.status);
    }
}