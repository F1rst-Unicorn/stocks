package de.njsm.stocks.server.v2.business;

import fj.data.Validation;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static junit.framework.TestCase.assertEquals;

public class StatusCodeTest {

    @Test(expected = RuntimeException.class)
    public void invalidValidationWithFailIsRaised() {
        StatusCode.toCode(Validation.fail(StatusCode.SUCCESS));
    }

    @Test(expected = RuntimeException.class)
    public void invalidValidationWithSuccessIsRaised() {
        StatusCode.toCode(Validation.success(StatusCode.DATABASE_UNREACHABLE));
    }

    @Test
    public void checkHttpCodeMappings() {
        assertEquals(Response.Status.OK, StatusCode.SUCCESS.toHttpStatus());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, StatusCode.GENERAL_ERROR.toHttpStatus());
        assertEquals(Response.Status.NOT_FOUND, StatusCode.NOT_FOUND.toHttpStatus());
        assertEquals(Response.Status.BAD_REQUEST, StatusCode.INVALID_DATA_VERSION.toHttpStatus());
        assertEquals(Response.Status.BAD_REQUEST, StatusCode.FOREIGN_KEY_CONSTRAINT_VIOLATION.toHttpStatus());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, StatusCode.DATABASE_UNREACHABLE.toHttpStatus());
        assertEquals(Response.Status.UNAUTHORIZED, StatusCode.ACCESS_DENIED.toHttpStatus());
        assertEquals(Response.Status.BAD_REQUEST, StatusCode.INVALID_ARGUMENT.toHttpStatus());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, StatusCode.CA_UNREACHABLE.toHttpStatus());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, StatusCode.SERIALISATION_CONFLICT.toHttpStatus());
    }
}