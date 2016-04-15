package de.njsm.stocks.endpoints;

import de.njsm.stocks.internal.Config;
import de.njsm.stocks.internal.auth.ContextFactory;
import de.njsm.stocks.internal.auth.UserContext;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

@Path("/test")
public class Test {

    protected Config c = new Config();

    @GET
    @Produces("text/plain")
    public String getMessage(@Context HttpServletRequest request) {
        UserContext context = c.getContextFactory().getUserContext(request);

        return context.getName() + "." + context.getDeviceName();



    }

}
