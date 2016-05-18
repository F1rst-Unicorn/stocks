package de.njsm.stocks.endpoints;

import de.njsm.stocks.data.Food;
import de.njsm.stocks.data.FoodItem;
import de.njsm.stocks.internal.auth.Principals;

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

        try {
            Principals uc = c.getContextFactory().getUserContext(request);
            handler.addFood(uc, food);
        } catch (SQLException e){
            c.getLog().log(Level.SEVERE, "FoodEndpoint: Failed to add food: " + e.getMessage());
        } catch (SecurityException e){
            c.getLog().log(Level.SEVERE, "FoodEndpoint: Failed to add food: " + e.getMessage());
        }
    }

    @PUT
    @Path("/{newname}")
    @Consumes("application/json")
    public void renameFood(@Context HttpServletRequest request,
                           Food food,
                           @PathParam("newname") String newName){


        c.getLog().log(Level.INFO, "FoodEndpoint: Renaming food " + food.name + " -> " + newName);

        try {
            Principals uc = c.getContextFactory().getUserContext(request);
            handler.renameFood(uc, food.id, newName);
        } catch (SQLException e){
            c.getLog().log(Level.SEVERE, "FoodEndpoint: Failed to rename food: " + e.getMessage());
        } catch (SecurityException e){
            c.getLog().log(Level.SEVERE, "FoodEndpoint: Failed to rename food: " + e.getMessage());
        }
    }

    @PUT
    @Path("/remove")
    @Consumes("application/json")
    public void removeFood(@Context HttpServletRequest request,
                           Food food) {

        c.getLog().log(Level.INFO, "FoodEndpoint: Removing food " + food.name);

        try {
            Principals uc = c.getContextFactory().getUserContext(request);
            handler.removeFood(uc, food.id);
        } catch (SQLException e){
            c.getLog().log(Level.SEVERE, "FoodEndpoint: Failed to remove food: " + e.getMessage());
        } catch (SecurityException e){
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

        c.getLog().log(Level.INFO, "FoodEndpoint: Add food item");

        try {
            Principals uc = c.getContextFactory().getUserContext(request);
            handler.addFoodItem(uc, item);
        } catch (SQLException e){
            c.getLog().log(Level.SEVERE, "FoodEndpoint: Failed to add food item: " + e.getMessage());
        } catch (SecurityException e){
            c.getLog().log(Level.SEVERE, "FoodEndpoint: Failed to add food item: " + e.getMessage());
        }
    }

    @PUT
    @Path("/fooditem/remove")
    @Consumes("application/json")
    public void removeFoodItem(@Context HttpServletRequest request,
                               FoodItem item){

        c.getLog().log(Level.INFO, "FoodEndpoint: Remove food item");

        try {
            Principals uc = c.getContextFactory().getUserContext(request);
            handler.removeFoodItem(uc, item.id);
        } catch (SQLException e){
            c.getLog().log(Level.SEVERE, "FoodEndpoint: Failed to remove food item: " + e.getMessage());
        } catch (SecurityException e){
            c.getLog().log(Level.SEVERE, "FoodEndpoint: Failed to remove food item: " + e.getMessage());
        }
    }
}
