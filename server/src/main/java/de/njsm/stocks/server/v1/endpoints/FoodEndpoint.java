package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.server.v1.internal.data.Data;
import de.njsm.stocks.server.v1.internal.data.Food;
import de.njsm.stocks.server.v1.internal.data.FoodFactory;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;

@Path("/food")
public class FoodEndpoint extends Endpoint {

    public FoodEndpoint(DatabaseHandler handler) {
        super(handler);
    }

    @GET
    @Produces("application/json")
    public Data[] getFood(@Context HttpServletRequest request) {
        return handler.get(FoodFactory.f);
    }

    @PUT
    @Consumes("application/json")
    public void addFood(@Context HttpServletRequest request,
                        Food foodToAdd){
        foodToAdd.id = 0;
        handler.add(foodToAdd);
    }

    @PUT
    @Path("/{newname}")
    @Consumes("application/json")
    public void renameFood(@Context HttpServletRequest request,
                           Food foodToRename,
                           @PathParam("newname") String newName){
        handler.rename(foodToRename, newName);
    }

    @PUT
    @Path("/remove")
    @Consumes("application/json")
    public void removeFood(@Context HttpServletRequest request,
                           Food foodToRemove) {
        handler.remove(foodToRemove);
    }

}
