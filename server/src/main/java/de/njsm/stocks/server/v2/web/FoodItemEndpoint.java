package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.FoodItem;
import de.njsm.stocks.server.v2.business.json.InstantDeserialiser;
import de.njsm.stocks.server.v2.db.FoodItemHandler;
import de.njsm.stocks.server.v2.web.data.ListResponse;
import de.njsm.stocks.server.v2.web.data.Response;
import fj.data.Validation;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Path("v2/fooditem")
public class FoodItemEndpoint extends Endpoint {

    private FoodItemHandler databaseHandler;

    public FoodItemEndpoint(FoodItemHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response putItem(@Context HttpServletRequest request,
                            @QueryParam("eatBy") String expirationDate,
                            @QueryParam("storedIn") int storedIn,
                            @QueryParam("ofType") int ofType) throws IOException {

        if (isValid(storedIn, "storedIn") &&
                isValid(ofType, "ofType") &&
                isValidInstant(expirationDate, "eatBy")) {

            Instant eatByDate = InstantDeserialiser.parseString(expirationDate);
            Principals user = getPrincipals(request);
            Validation<StatusCode, Integer> status = databaseHandler.add(new FoodItem(eatByDate,
                    ofType, storedIn, user.getDid(), user.getUid()));
            return new Response(status);

        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ListResponse<FoodItem> getItems() {
        Validation<StatusCode, List<FoodItem>> result = databaseHandler.get();
        return new ListResponse<>(result);
    }

    @PUT
    @Path("edit")
    @Produces(MediaType.APPLICATION_JSON)
    public Response editItem(@Context HttpServletRequest request,
                             @QueryParam("id") int id,
                             @QueryParam("version") int version,
                             @QueryParam("eatBy") String expirationDate,
                             @QueryParam("storedIn") int storedIn) throws IOException {
        if (isValid(id, "id") &&
                isValidVersion(version, "version") &&
                isValidInstant(expirationDate, "eatBy") &&
                isValid(storedIn, "storedIn")) {

            Instant eatByDate = InstantDeserialiser.parseString(expirationDate);
            Principals user = getPrincipals(request);
            StatusCode result = databaseHandler.edit(new FoodItem(id, version,
                    eatByDate, 0, storedIn, user.getDid(), user.getUid()));

            return new Response(result);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteItem(@QueryParam("id") int id,
                               @QueryParam("version") int version) {

        if (isValid(id, "id") &&
                isValidVersion(version, "version")) {
            StatusCode status = databaseHandler.delete(new FoodItem(id, version));
            return new Response(status);
        } else {
            return new Response(StatusCode.INVALID_ARGUMENT);
        }
    }


}
