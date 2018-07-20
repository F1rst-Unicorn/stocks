package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.Food;
import de.njsm.stocks.server.v2.db.FoodHandler;
import de.njsm.stocks.server.v2.web.data.ListResponse;
import de.njsm.stocks.server.v2.web.data.Response;
import fj.data.Validation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("v2/food")
public class FoodEndpoint extends Endpoint {

    private FoodHandler databaseHandler;

    public FoodEndpoint(FoodHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response putFood(@QueryParam("name") String name) {
        StatusCode status = databaseHandler.add(new Food(name));
        return new Response(status);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ListResponse<Food> getFood() {
        Validation<StatusCode, List<Food>> result = databaseHandler.get();
        return new ListResponse<>(result);
    }

    @PUT
    @Path("rename")
    @Produces(MediaType.APPLICATION_JSON)
    public Response renameFood(@QueryParam("id") int id,
                               @QueryParam("version") int version,
                               @QueryParam("new") String newName) {
        StatusCode status = databaseHandler.rename(new Food(id, "", version), newName);
        return new Response(status);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteFood(@QueryParam("id") int id,
                               @QueryParam("version") int version) {
        StatusCode status = databaseHandler.delete(new Food(id, "", version));
        return new Response(status);
    }


}
