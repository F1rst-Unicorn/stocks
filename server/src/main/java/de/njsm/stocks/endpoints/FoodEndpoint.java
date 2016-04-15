package de.njsm.stocks.endpoints;

import de.njsm.stocks.data.Food;
import de.njsm.stocks.data.FoodItem;
import de.njsm.stocks.internal.auth.UserContext;
import de.njsm.stocks.internal.db.DatabaseHandler;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.sql.SQLException;

@Path("/food")
public class FoodEndpoint extends Endpoint {

    @GET
    @Produces("application/json")
    public Food[] getFood() {

        try {
            return handler.getFood();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PUT
    @Consumes("application/json")
    public void addFood(@Context HttpServletRequest request, Food food){
        UserContext uc = c.getContextFactory().getUserContext(request);

        try {
            handler.addFood(uc, food);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @PUT
    @Path("/{newname}")
    @Consumes("application/json")
    public void renameFood(@Context HttpServletRequest request,
                           Food food,
                           @PathParam("newname") String newName){
        UserContext uc = c.getContextFactory().getUserContext(request);

        try {
            handler.renameFood(uc, food.id, newName);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @POST
    @Consumes("application/json")
    public void removeFood(@Context HttpServletRequest request,
                           Food food) {
        UserContext uc = c.getContextFactory().getUserContext(request);

        try {
            handler.removeFood(uc, food.id);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @GET
    @Path("/fooditem")
    @Produces("application/json")
    public FoodItem[] getFoodItems() {

        try {
            return handler.getFoodItems();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PUT
    @Path("/fooditem")
    @Consumes("application/json")
    public void addFoodItem(@Context HttpServletRequest request, FoodItem item){
        UserContext uc = c.getContextFactory().getUserContext(request);

        try {
            handler.addFoodItem(uc, item);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @POST
    @Path("/fooditem")
    @Consumes("application/json")
    public void removeFoodItem(@Context HttpServletRequest request,
                               FoodItem item){
        UserContext uc = c.getContextFactory().getUserContext(request);

        try {
            handler.removeFoodItem(uc, item.id);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
