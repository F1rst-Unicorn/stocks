package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.server.data.Data;
import de.njsm.stocks.server.data.UpdateFactory;
import de.njsm.stocks.server.internal.auth.Principals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import java.util.logging.Level;

@Path("/update")
public class UpdateEndpoint extends Endpoint {

    @GET
    @Produces("application/json")
    public Data[] getUpdates(@Context HttpServletRequest request) {
        Principals uc = c.getContextFactory().getPrincipals(request);
        c.getLog().log(Level.INFO, uc.getUsername() + "@" + uc.getDeviceName() + " gets updates");
        return handler.get(UpdateFactory.f);
    }
}
