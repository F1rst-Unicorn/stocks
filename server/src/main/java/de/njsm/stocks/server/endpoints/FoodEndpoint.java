package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.server.data.Food;
import de.njsm.stocks.server.data.FoodItem;
import de.njsm.stocks.server.internal.auth.Principals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.sql.SQLException;
import java.util.logging.Level;

@Path("/food")
public class FoodEndpoint extends Endpoint {

    @GET
    @Produces("application/json")
    public Food[] getFood() {
        c.getLog().log(Level.INFO, "FoodEndpoint: Get food");
        try {
            return handler.getFood();
        } catch (SQLException e) {
            c.getLog().log(Level.SEVERE, "FoodEndpoint: Failed to get food: " + e.getMessage());
        }
        return null;
    }

    @PUT
    @Consumes("application/json")
    public void addFood(@Context HttpServletRequest request, Food food){
        c.getLog().log(Level.INFO, "FoodEndpoint: Adding food " + food.name);
        handler.add(food);
    }

    @PUT
    @Path("/{newname}")
    @Consumes("application/json")
    public void renameFood(@Context HttpServletRequest request,
                           Food food,
                           @PathParam("newname") String newName){

        c.getLog().log(Level.INFO, "FoodEndpoint: Renaming food " + food.name + " -> " + newName);
        handler.rename(food, newName);
    }

    @PUT
    @Path("/remove")
    @Consumes("application/json")
    public void removeFood(@Context HttpServletRequest request,
                           Food food) {

        c.getLog().log(Level.INFO, "FoodEndpoint: Removing food " + food.name);

        try {
            handler.removeFood(food.id);
        } catch (SQLException | SecurityException e){
            c.getLog().log(Level.SEVERE, "FoodEndpoint: Failed to remove food: " + e.getMessage());
        }
    }

    @GET
    @Path("/fooditem")
    @Produces("application/json")
    public FoodItem[] getFoodItems() {

        c.getLog().log(Level.INFO, "FoodEndpoint: Get food items");
        try {
            return handler.getFoodItems();
        } catch (SQLException e) {
            c.getLog().log(Level.SEVERE, "FoodEndpoint: Failed to get food items: " + e.getMessage());
        }
        return null;
    }

    @PUT
    @Path("/fooditem")
    @Consumes("application/json")
    public void addFoodItem(@Context HttpServletRequest request, FoodItem item){

        try {
            Principals uc = c.getContextFactory().getPrincipals(request);
            c.getLog().log(Level.INFO, "FoodEndpoint: Add food item");
            item.buys = uc.getUid();
            item.registers = uc.getDid();
            handler.add(item);
        } catch (SecurityException e) {
            c.getLog().log(Level.SEVERE, "FoodEndpoint: security violation: " + e.getMessage());
        }
    }

    @PUT
    @Path("/fooditem/remove")
    @Consumes("application/json")
    public void removeFoodItem(@Context HttpServletRequest request,
                               FoodItem item){

        c.getLog().log(Level.INFO, "FoodEndpoint: Remove food item");

        try {
            handler.removeFoodItem(item.id);
        } catch (SQLException | SecurityException e){
            c.getLog().log(Level.SEVERE, "FoodEndpoint: Failed to remove food item: " + e.getMessage());
        }
    }
}
