package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.server.data.Data;
import de.njsm.stocks.server.data.User;
import de.njsm.stocks.server.data.UserFactory;

import javax.ws.rs.*;
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
    public void addUser(User u) {
        if (isNameValid(u.name)) {
            handler.add(u);
            c.getLog().log(Level.INFO, "UserEndpoint: Add user " + u.name);
        } else {
            c.getLog().log(Level.INFO, "UserEndpoint: Tried to add invalid user " + u.name);
        }
    }

    @GET
    @Produces("application/json")
    public Data[] getUsers() {
        c.getLog().log(Level.INFO, "UserEndpoint: Get users");
        return handler.get(UserFactory.f);
    }

    @PUT
    @Path("/remove")
    @Consumes("application/json")
    public void deleteUser(User u) {
        c.getLog().log(Level.INFO, "UserEndpoint: Delete user " + u.name);
        handler.removeUser(u);
    }

}
