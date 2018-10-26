package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.Update;
import de.njsm.stocks.server.v2.db.UpdateBackend;
import de.njsm.stocks.server.v2.web.data.ListResponse;
import fj.data.Validation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

@Path("/v2/update")
public class UpdateEndpoint {

    private UpdateBackend handler;

    public UpdateEndpoint(UpdateBackend handler) {
        this.handler = handler;
    }

    @GET
    @Produces("application/json")
    public ListResponse<Update> getUpdates() {
        Validation<StatusCode, List<Update>> result = handler.getUpdates();
        return new ListResponse<>(result);
    }
}
