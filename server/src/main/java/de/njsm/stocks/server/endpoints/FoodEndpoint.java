package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.server.data.*;
import de.njsm.stocks.server.internal.Config;
import de.njsm.stocks.server.internal.auth.Principals;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/food")
public class FoodEndpoint extends Endpoint {

    private static final Logger LOG = LogManager.getLogger(FoodEndpoint.class);

    public FoodEndpoint() {
    }

    public FoodEndpoint(Config c) {
        super(c);
    }

    @GET
    @Produces("application/json")
    public Data[] getFood(@Context HttpServletRequest request) {
        logAccess(LOG, request, "gets food");
        return handler.get(FoodFactory.f);
    }

    @PUT
    @Consumes("application/json")
    public void addFood(@Context HttpServletRequest request,
                        Food foodToAdd){
        logAccess(LOG, request, "adds food " + foodToAdd.name);
        handler.add(foodToAdd);
    }

    @PUT
    @Path("/{newname}")
    @Consumes("application/json")
    public void renameFood(@Context HttpServletRequest request,
                           Food foodToRename,
                           @PathParam("newname") String newName){
        logAccess(LOG, request, "renames food "
                + foodToRename.name  + " -> " + newName);
        handler.rename(foodToRename, newName);
    }

    @PUT
    @Path("/remove")
    @Consumes("application/json")
    public void removeFood(@Context HttpServletRequest request,
                           Food foodToRemove) {
        logAccess(LOG, request, "removes food " + foodToRemove.name);
        handler.remove(foodToRemove);
    }

    @GET
    @Path("/fooditem")
    @Produces("application/json")
    public Data[] getFoodItems(@Context HttpServletRequest request) {
        logAccess(LOG, request, "gets food items");
        return handler.get(FoodItemFactory.f);
    }

    @PUT
    @Path("/fooditem")
    @Consumes("application/json")
    public void addFoodItem(@Context HttpServletRequest request,
                            FoodItem itemToAdd){
        logAccess(LOG, request, "adds food item" + itemToAdd.ofType);

        Principals uc = c.getContextFactory().getPrincipals(request);
        itemToAdd.buys = uc.getUid();
        itemToAdd.registers = uc.getDid();

        handler.add(itemToAdd);
    }

    @PUT
    @Path("/fooditem/remove")
    @Consumes("application/json")
    public void removeFoodItem(@Context HttpServletRequest request,
                               FoodItem itemToRemove){
        logAccess(LOG, request, "removes food item " + itemToRemove.id);
        handler.remove(itemToRemove);
    }

    @PUT
    @Path("/fooditem/move/{newId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void moveFoodItem(@Context HttpServletRequest request,
                             FoodItem itemToMove,
                             @PathParam("newId") int newLocationId) {
        logAccess(LOG, request, "moves food item " + itemToMove.id
                + " to " + newLocationId);
        handler.moveItem(itemToMove, newLocationId);
    }
}
