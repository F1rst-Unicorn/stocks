package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.User;
import de.njsm.stocks.server.v1.internal.business.UserContextFactory;
import de.njsm.stocks.server.v1.internal.business.UserManager;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;

@Path("/user")
public class UserEndpoint extends Endpoint {

    private static final Logger LOG = LogManager.getLogger(UserEndpoint.class);

    private UserManager userManager;

    public UserEndpoint(UserManager userManager,
                        DatabaseHandler handler,
                        UserContextFactory contextFactory) {
        super(handler, contextFactory);
        this.userManager = userManager;
    }

    @PUT
    @Consumes("application/json")
    public void addUser(@Context HttpServletRequest request,
                        User userToAdd) {

        logAccess(LOG, request, "adds user " + userToAdd.name);
        userManager.addUser(userToAdd);
    }

    @GET
    @Produces("application/json")
    public Data[] getUsers(@Context HttpServletRequest request) {
        logAccess(LOG, request, "gets users");
        return userManager.getUsers();
    }

    @PUT
    @Path("/remove")
    @Consumes("application/json")
    public void removeUser(@Context HttpServletRequest request,
                           User userToDelete) {
        logAccess(LOG, request, "removes user " + userToDelete.name);
        userManager.removeUser(userToDelete);
    }

}
