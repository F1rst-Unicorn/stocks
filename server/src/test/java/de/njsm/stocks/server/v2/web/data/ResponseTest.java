package de.njsm.stocks.server.v2.web.data;

import de.njsm.stocks.server.v2.business.StatusCode;
import fj.data.Validation;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ResponseTest {

    @Test
    public void failedValidationCodeIsSet() {
        Validation<StatusCode, String> input = Validation.fail(StatusCode.NOT_FOUND);

        Response uut = new Response(input);

        assertEquals(StatusCode.NOT_FOUND, uut.status);
    }
}