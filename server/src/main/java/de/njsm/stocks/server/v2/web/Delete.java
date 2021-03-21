package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.Entity;
import de.njsm.stocks.server.v2.business.data.Versionable;
import de.njsm.stocks.server.v2.web.data.Response;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import static de.njsm.stocks.server.v2.web.Endpoint.isValid;
import static de.njsm.stocks.server.v2.web.Endpoint.isValidVersion;

public interface Delete<T extends Versionable<U>, U extends Entity<U>> extends MetaDelete<T, U> {

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    default Response delete(@Context HttpServletRequest request,
                                   @QueryParam("id") int id,
                                   @QueryParam("version") int version) {
        if (isValid(id, "id") &&
                isValidVersion(version, "version")) {
            return delete(request, () -> wrapParameters(id, version));
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    T wrapParameters(int id, int version);
}
