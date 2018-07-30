package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.UpdateFactory;
import de.njsm.stocks.server.v1.internal.business.UserContextFactory;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

@Path("/update")
public class UpdateEndpoint extends Endpoint {

    public UpdateEndpoint(DatabaseHandler handler,
                          UserContextFactory contextFactory) {
        super(handler, contextFactory);
    }

    @GET
    @Produces("application/json")
    public Data[] getUpdates(@Context HttpServletRequest request) {
        return handler.get(UpdateFactory.f);
    }
}
