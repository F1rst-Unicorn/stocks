package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.server.data.Data;
import de.njsm.stocks.server.data.User;
import de.njsm.stocks.server.data.UserFactory;
import de.njsm.stocks.server.internal.auth.Principals;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;

@Path("/user")
public class UserEndpoint extends Endpoint {

    private static final Logger LOG = LogManager.getLogger(UserEndpoint.class);

    private static boolean isNameValid(String name) {
        int noDollar = name.indexOf('$');
        int noEqual  = name.indexOf('=');
        return noDollar == -1 && noEqual == -1;
    }

    @PUT
    @Consumes("application/json")
    public void addUser(@Context HttpServletRequest request, User u) {
        Principals uc = c.getContextFactory().getPrincipals(request);
        if (isNameValid(u.name)) {
            handler.add(u);
            LOG.info(uc.getUsername() + "@" + uc.getDeviceName() + " adds user " + u.name);
        } else {
            LOG.warn(uc.getUsername() + "@" + uc.getDeviceName() + " tried to add invalid user " + u.name);
        }
    }

    @GET
    @Produces("application/json")
    public Data[] getUsers(@Context HttpServletRequest request) {
        Principals uc = c.getContextFactory().getPrincipals(request);
        LOG.info(uc.getUsername() + "@" + uc.getDeviceName() + " gets users");
        return handler.get(UserFactory.f);
    }

    @PUT
    @Path("/remove")
    @Consumes("application/json")
    public void deleteUser(@Context HttpServletRequest request, User u) {
        Principals uc = c.getContextFactory().getPrincipals(request);
        LOG.info(uc.getUsername() + "@" + uc.getDeviceName() + " removes user " + u.name);
        handler.removeUser(u);
    }

}
