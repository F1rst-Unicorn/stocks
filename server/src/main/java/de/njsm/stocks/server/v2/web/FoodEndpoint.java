package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.v2.business.BusinessManager;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.Food;
import de.njsm.stocks.server.v2.web.data.DataResponse;
import de.njsm.stocks.server.v2.web.data.Response;
import fj.data.Validation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("v2/food")
public class FoodEndpoint extends Endpoint {

    private BusinessManager businessManager;

    public FoodEndpoint(BusinessManager businessManager) {
        this.businessManager = businessManager;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response putFood(@QueryParam("name") String name) {
        StatusCode status = businessManager.addFood(name);
        return new Response(status);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public DataResponse<Food> getFood() {
        Validation<StatusCode, List<Food>> result = businessManager.getFood();
        return new DataResponse<>(result);
    }

    @PUT
    @Path("rename")
    @Produces(MediaType.APPLICATION_JSON)
    public Response renameFood(@QueryParam("id") int id,
                               @QueryParam("version") int version,
                               @QueryParam("new") String newName) {
        StatusCode status = businessManager.renameFood(id, version, newName);
        return new Response(status);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteFood(@QueryParam("id") int id,
                               @QueryParam("version") int version) {
        StatusCode status = businessManager.deleteFood(id, version);
        return new Response(status);
    }


}
