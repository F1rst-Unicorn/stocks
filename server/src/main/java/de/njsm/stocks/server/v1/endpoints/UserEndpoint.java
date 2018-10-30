package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.server.v1.internal.data.Data;
import de.njsm.stocks.server.v1.internal.data.User;
import de.njsm.stocks.server.v1.internal.business.UserManager;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;

@Path("/user")
public class UserEndpoint extends Endpoint {

    private UserManager userManager;

    public UserEndpoint(UserManager userManager,
                        DatabaseHandler handler) {
        super(handler);
        this.userManager = userManager;
    }

    @PUT
    @Consumes("application/json")
    public void addUser(@Context HttpServletRequest request,
                        User userToAdd) {

        userToAdd.id = 0;
        userManager.addUser(userToAdd);
    }

    @GET
    @Produces("application/json")
    public Data[] getUsers(@Context HttpServletRequest request) {
        return userManager.getUsers();
    }

    @PUT
    @Path("/remove")
    @Consumes("application/json")
    public void removeUser(@Context HttpServletRequest request,
                           User userToDelete) {
        userManager.removeUser(userToDelete);
    }

}
