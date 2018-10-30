package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.server.v1.internal.data.Data;
import de.njsm.stocks.server.v1.internal.data.FoodItem;
import de.njsm.stocks.server.v1.internal.data.FoodItemFactory;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/food/fooditem")
public class FoodItemEndpoint extends Endpoint {

    public FoodItemEndpoint(DatabaseHandler handler) {
        super(handler);
    }

    @GET
    @Produces("application/json")
    public Data[] getFoodItems(@Context HttpServletRequest request) {
        return handler.get(FoodItemFactory.f);
    }
    @PUT
    @Consumes("application/json")
    public void addFoodItem(@Context HttpServletRequest request,
                            FoodItem itemToAdd){

        Principals uc = getPrincipals(request);
        itemToAdd.buys = uc.getUid();
        itemToAdd.registers = uc.getDid();
        itemToAdd.id = 0;

        handler.add(itemToAdd);
    }

    @PUT
    @Path("/remove")
    @Consumes("application/json")
    public void removeFoodItem(@Context HttpServletRequest request,
                               FoodItem itemToRemove){
        handler.remove(itemToRemove);
    }

    @PUT
    @Path("/move/{newId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void moveFoodItem(@Context HttpServletRequest request,
                             FoodItem itemToMove,
                             @PathParam("newId") int newLocationId) {
        handler.moveItem(itemToMove, newLocationId);
    }
}
