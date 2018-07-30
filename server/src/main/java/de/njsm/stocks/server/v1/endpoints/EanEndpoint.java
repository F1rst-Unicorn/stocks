package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.EanNumber;
import de.njsm.stocks.common.data.EanNumberFactory;
import de.njsm.stocks.server.v1.internal.business.UserContextFactory;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/ean")
public class EanEndpoint extends Endpoint {

    public EanEndpoint(DatabaseHandler handler,
                       UserContextFactory contextFactory) {
        super(handler, contextFactory);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Data[] getEanNumbers(@Context HttpServletRequest request) {
        return handler.get(EanNumberFactory.f);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void addEanNumber(@Context HttpServletRequest request,
                             EanNumber numberToAdd) {
        numberToAdd.id = 0;
        handler.add(numberToAdd);
    }

    @PUT
    @Path("/remove")
    @Consumes(MediaType.APPLICATION_JSON)
    public void removeEanNumber(@Context HttpServletRequest request,
                                EanNumber numberToRemove) {
        handler.remove(numberToRemove);
    }
}
