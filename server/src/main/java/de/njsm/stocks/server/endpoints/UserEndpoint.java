package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.server.data.Data;
import de.njsm.stocks.server.data.User;
import de.njsm.stocks.server.data.UserFactory;
import de.njsm.stocks.server.internal.auth.Principals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.logging.Level;

@Path("/user")
public class UserEndpoint extends Endpoint {

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
            c.getLog().log(Level.INFO, uc.getUsername() + "@" + uc.getDeviceName() + " adds user " + u.name);
        } else {
            c.getLog().log(Level.WARNING, uc.getUsername() + "@" + uc.getDeviceName() + " tried to add invalid user " + u.name);
        }
    }

    @GET
    @Produces("application/json")
    public Data[] getUsers(@Context HttpServletRequest request) {
        Principals uc = c.getContextFactory().getPrincipals(request);
        c.getLog().log(Level.INFO, uc.getUsername() + "@" + uc.getDeviceName() + " gets users");
        return handler.get(UserFactory.f);
    }

    @PUT
    @Path("/remove")
    @Consumes("application/json")
    public void deleteUser(@Context HttpServletRequest request, User u) {
        Principals uc = c.getContextFactory().getPrincipals(request);
        c.getLog().log(Level.INFO, uc.getUsername() + "@" + uc.getDeviceName() + " removes user " + u.name);
        handler.removeUser(u);
    }

}
