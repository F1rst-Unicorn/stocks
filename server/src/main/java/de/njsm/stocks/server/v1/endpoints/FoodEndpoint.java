package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.server.v2.business.data.Food;
import de.njsm.stocks.server.v2.db.FoodHandler;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.LinkedList;
import java.util.List;

@Path("/food")
public class FoodEndpoint extends de.njsm.stocks.server.v2.web.Endpoint {

    private FoodHandler handler;

    public FoodEndpoint(FoodHandler handler) {
        this.handler = handler;
    }

    @GET
    @Produces("application/json")
    public Food[] getFood(@Context HttpServletRequest request) {
        List<Food> list = handler.get().orSuccess(new LinkedList<>());
        return list.toArray(new Food[0]);
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
        handler.delete(foodToRemove);
    }
}
