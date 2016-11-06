package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.server.data.*;
import de.njsm.stocks.server.internal.auth.Principals;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.logging.Level;

@Path("/food")
public class FoodEndpoint extends Endpoint {

    private static final Logger LOG = LogManager.getLogger(FoodEndpoint.class);

    @GET
    @Produces("application/json")
    public Data[] getFood(@Context HttpServletRequest request) {
        Principals uc = c.getContextFactory().getPrincipals(request);
        LOG.info(uc.getUsername() + "@" + uc.getDeviceName() + " gets food");

        return handler.get(FoodFactory.f);
    }

    @PUT
    @Consumes("application/json")
    public void addFood(@Context HttpServletRequest request, Food food){
        Principals uc = c.getContextFactory().getPrincipals(request);
        LOG.info(uc.getUsername() + "@" + uc.getDeviceName() + " adds food " + food.name);

        handler.add(food);
    }

    @PUT
    @Path("/{newname}")
    @Consumes("application/json")
    public void renameFood(@Context HttpServletRequest request,
                           Food food,
                           @PathParam("newname") String newName){

        Principals uc = c.getContextFactory().getPrincipals(request);
        LOG.info(uc.getUsername() + "@" + uc.getDeviceName() +
                " renames food " + food.name + " -> " + newName);

        handler.rename(food, newName);
    }

    @PUT
    @Path("/remove")
    @Consumes("application/json")
    public void removeFood(@Context HttpServletRequest request,
                           Food food) {

        Principals uc = c.getContextFactory().getPrincipals(request);
        LOG.info(uc.getUsername() + "@" + uc.getDeviceName() + " removes food " + food.name);

        handler.remove(food);
    }

    @GET
    @Path("/fooditem")
    @Produces("application/json")
    public Data[] getFoodItems(@Context HttpServletRequest request) {
        Principals uc = c.getContextFactory().getPrincipals(request);
        LOG.info(uc.getUsername() + "@" + uc.getDeviceName() + " gets food items");

        return handler.get(FoodItemFactory.f);
    }

    @PUT
    @Path("/fooditem")
    @Consumes("application/json")
    public void addFoodItem(@Context HttpServletRequest request, FoodItem item){

        try {
            Principals uc = c.getContextFactory().getPrincipals(request);
            LOG.info(uc.getUsername() + "@" + uc.getDeviceName() + " adds food item " + item.id);
            item.buys = uc.getUid();
            item.registers = uc.getDid();
            handler.add(item);
        } catch (SecurityException e) {
            LOG.error("Security violation", e);
        }
    }

    @PUT
    @Path("/fooditem/remove")
    @Consumes("application/json")
    public void removeFoodItem(@Context HttpServletRequest request,
                               FoodItem item){

        Principals uc = c.getContextFactory().getPrincipals(request);
        LOG.info(uc.getUsername() + "@" + uc.getDeviceName() + " removes food item " + item.id);
        handler.remove(item);
    }

    @PUT
    @Path("/fooditem/move/{newId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void moveFoodItem(@Context HttpServletRequest request,
                             FoodItem item,
                             @PathParam("newId") int newLocId) {
        Principals uc = c.getContextFactory().getPrincipals(request);
        LOG.info(uc.getUsername() + "@" + uc.getDeviceName() + " moves food item " + item.id +
                " to " + newLocId);
        handler.moveItem(item, newLocId);
    }
}
