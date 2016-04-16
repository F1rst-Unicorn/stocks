package de.njsm.stocks.endpoints;

import de.njsm.stocks.data.User;

import javax.ws.rs.*;
import java.sql.SQLException;

@Path("/user")
public class UserEndpoint extends Endpoint {

    @GET
    @Path("/newuser")
    @Produces("application/json")
    public String getTicket() {
        try {
            return handler.getNewTicket();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "";
    }

    @GET
    @Produces("application/json")
    public User[] getUsers() {
        try {
            return handler.getUsers();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @PUT
    @Consumes("application/json")
    public void deleteUser(User u) {
        try {
            handler.removeUser(u.id);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

}
