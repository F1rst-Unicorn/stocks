package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.UpdateFactory;
import de.njsm.stocks.server.util.UserContextFactory;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

@Path("/update")
public class UpdateEndpoint extends Endpoint {

    private static final Logger LOG = LogManager.getLogger(UpdateEndpoint.class);

    public UpdateEndpoint(DatabaseHandler handler,
                          UserContextFactory contextFactory) {
        super(handler, contextFactory);
    }

    @GET
    @Produces("application/json")
    public Data[] getUpdates(@Context HttpServletRequest request) {
        logAccess(LOG, request, "gets updates");
        return handler.get(UpdateFactory.f);
    }
}
