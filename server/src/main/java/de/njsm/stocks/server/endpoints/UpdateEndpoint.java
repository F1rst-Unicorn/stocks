package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.server.data.Data;
import de.njsm.stocks.server.data.UpdateFactory;
import de.njsm.stocks.server.internal.auth.Principals;
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

    @GET
    @Produces("application/json")
    public Data[] getUpdates(@Context HttpServletRequest request) {
        Principals uc = c.getContextFactory().getPrincipals(request);
        LOG.info(uc.getUsername() + "@" + uc.getDeviceName() + " gets updates");
        return handler.get(UpdateFactory.f);
    }
}
