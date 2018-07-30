package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.common.data.*;
import de.njsm.stocks.server.v1.internal.business.UserContextFactory;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/food")
public class FoodEndpoint extends Endpoint {

    public FoodEndpoint(DatabaseHandler handler,
                        UserContextFactory contextFactory) {
        super(handler, contextFactory);
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

    @GET
    @Path("/fooditem")
    @Produces("application/json")
    public Data[] getFoodItems(@Context HttpServletRequest request) {
        return handler.get(FoodItemFactory.f);
    }

    @PUT
    @Path("/fooditem")
    @Consumes("application/json")
    public void addFoodItem(@Context HttpServletRequest request,
                            FoodItem itemToAdd){

        Principals uc = contextFactory.getPrincipals(request);
        itemToAdd.buys = uc.getUid();
        itemToAdd.registers = uc.getDid();
        itemToAdd.id = 0;

        handler.add(itemToAdd);
    }

    @PUT
    @Path("/fooditem/remove")
    @Consumes("application/json")
    public void removeFoodItem(@Context HttpServletRequest request,
                               FoodItem itemToRemove){
        handler.remove(itemToRemove);
    }

    @PUT
    @Path("/fooditem/move/{newId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void moveFoodItem(@Context HttpServletRequest request,
                             FoodItem itemToMove,
                             @PathParam("newId") int newLocationId) {
        handler.moveItem(itemToMove, newLocationId);
    }
}
