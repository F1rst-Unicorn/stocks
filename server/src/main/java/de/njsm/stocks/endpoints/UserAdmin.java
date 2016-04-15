package de.njsm.stocks.endpoints;

import de.njsm.stocks.data.User;

import javax.ws.rs.*;

@Path("/uac")
public class UserAdmin {


    @PUT
    @Path("/{user}")
    public void addNewUser(@PathParam("user") String username){

    }


}
