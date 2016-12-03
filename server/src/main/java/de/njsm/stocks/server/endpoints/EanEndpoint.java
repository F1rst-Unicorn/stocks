package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.server.data.Data;
import de.njsm.stocks.server.data.EanNumber;
import de.njsm.stocks.server.data.EanNumberFactory;
import de.njsm.stocks.server.internal.auth.Principals;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

public class EanEndpoint extends Endpoint {

    private static final Logger LOG = LogManager.getLogger(EanEndpoint.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Data[] getEanNumbers(@Context HttpServletRequest request) {
        Principals uc = c.getContextFactory().getPrincipals(request);
        LOG.info(uc.getUsername() + "@" + uc.getDeviceName() + " gets EAN numbers");

        return handler.get(EanNumberFactory.f);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void addEanNumber(@Context HttpServletRequest request, EanNumber numberToAdd) {
        Principals uc = c.getContextFactory().getPrincipals(request);
        LOG.info(uc.getUsername() + "@" + uc.getDeviceName() + " adds EAN number " + numberToAdd.eanCode);

        handler.add(numberToAdd);
    }

    @PUT
    @Path("/remove")
    @Consumes(MediaType.APPLICATION_JSON)
    public void removeEanNumber(@Context HttpServletRequest request, EanNumber numberToRemove) {
        Principals uc = c.getContextFactory().getPrincipals(request);
        LOG.info(uc.getUsername() + "@" + uc.getDeviceName() + " removes EAN number " + numberToRemove.eanCode);

        handler.remove(numberToRemove);
    }
}
