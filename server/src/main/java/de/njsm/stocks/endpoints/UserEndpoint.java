package de.njsm.stocks.endpoints;

import de.njsm.stocks.data.User;

import javax.ws.rs.*;
import java.sql.SQLException;
import java.util.logging.Level;

@Path("/user")
public class UserEndpoint extends Endpoint {

    @GET
    @Path("/newuser")
    @Produces("application/json")
    public String getTicket() {

        c.getLog().log(Level.INFO, "UserEndpoint: Added new ticket");
        try {
            return handler.getNewTicket();
        } catch (SQLException e) {
            c.getLog().log(Level.SEVERE, "UserEndpoint: Failed to add ticket: " + e.getMessage());
        }

        return "";
    }

    @GET
    @Produces("application/json")
    public User[] getUsers() {
        c.getLog().log(Level.INFO, "UserEndpoint: Get users");
        try {
            return handler.getUsers();
        } catch (SQLException e){
            c.getLog().log(Level.SEVERE, "UserEndpoint: Failed to get users: " + e.getMessage());
        }
        return null;
    }

    @PUT
    @Consumes("application/json")
    public void deleteUser(User u) {
        c.getLog().log(Level.INFO, "UserEndpoint: Delete user " + u.name);
        try {
            handler.removeUser(u.id);
        } catch (SQLException e){
            c.getLog().log(Level.SEVERE, "UserEndpoint: Failed to delete user: " + e.getMessage());
        }
    }

}
