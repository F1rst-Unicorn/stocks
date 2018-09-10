package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.EanNumber;
import de.njsm.stocks.server.v2.db.EanNumberHandler;
import de.njsm.stocks.server.v2.web.data.ListResponse;
import de.njsm.stocks.server.v2.web.data.Response;
import fj.data.Validation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("v2/ean")
public class EanNumberEndpoint extends Endpoint {

    private EanNumberHandler databaseHandler;

    public EanNumberEndpoint(EanNumberHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response putEanNumber(@QueryParam("code") String code,
                                 @QueryParam("identifies") int foodId) {
        StatusCode status = databaseHandler.add(new EanNumber(code, foodId));
        return new Response(status);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ListResponse<EanNumber> getEanNumbers() {
        Validation<StatusCode, List<EanNumber>> result = databaseHandler.get();
        return new ListResponse<>(result);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteEanNumber(@QueryParam("id") int id,
                                    @QueryParam("version") int version) {
        StatusCode status = databaseHandler.delete(new EanNumber(id, version, "", 0));
        return new Response(status);
    }


}
