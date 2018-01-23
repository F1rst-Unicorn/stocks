package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.Principals;
import de.njsm.stocks.common.data.User;
import de.njsm.stocks.common.data.UserFactory;
import de.njsm.stocks.server.internal.auth.HttpsUserContextFactory;
import de.njsm.stocks.server.internal.auth.UserContextFactory;
import de.njsm.stocks.server.internal.db.DatabaseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;

@Path("/user")
public class UserEndpoint extends Endpoint {

    private static final Logger LOG = LogManager.getLogger(UserEndpoint.class);

    public UserEndpoint(DatabaseHandler handler,
                        UserContextFactory contextFactory) {
        super(handler, contextFactory);
    }

    @PUT
    @Consumes("application/json")
    public void addUser(@Context HttpServletRequest request,
                        User userToAdd) {

        if (HttpsUserContextFactory.isNameValid(userToAdd.name)) {
            logAccess(LOG, request, "adds user " + userToAdd.name);
            handler.add(userToAdd);

        } else {
            Principals client = contextFactory.getPrincipals(request);
            LOG.warn(client.getReadableString()
                    + "tried to add invalid user " + userToAdd.name);
        }
    }

    @GET
    @Produces("application/json")
    public Data[] getUsers(@Context HttpServletRequest request) {
        logAccess(LOG, request, "gets users");
        return handler.get(UserFactory.f);
    }

    @PUT
    @Path("/remove")
    @Consumes("application/json")
    public void removeUser(@Context HttpServletRequest request,
                           User userToDelete) {
        logAccess(LOG, request, "removes user " + userToDelete.name);
        handler.removeUser(userToDelete);
    }

}
