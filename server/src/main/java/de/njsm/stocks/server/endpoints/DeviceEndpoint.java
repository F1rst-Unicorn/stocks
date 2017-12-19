package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.Ticket;
import de.njsm.stocks.common.data.UserDevice;
import de.njsm.stocks.common.data.UserDeviceFactory;
import de.njsm.stocks.server.internal.auth.HttpsUserContextFactory;
import de.njsm.stocks.server.internal.auth.Principals;
import de.njsm.stocks.server.internal.auth.UserContextFactory;
import de.njsm.stocks.server.internal.db.DatabaseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/device")
public class DeviceEndpoint extends Endpoint {

    private static final Logger LOG = LogManager.getLogger(DeviceEndpoint.class);

    public DeviceEndpoint(DatabaseHandler handler,
                          UserContextFactory contextFactory) {
        super(handler, contextFactory);
    }

    @PUT
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Ticket addDevice(@Context HttpServletRequest request,
                            UserDevice deviceToAdd) {

        if (HttpsUserContextFactory.isNameValid(deviceToAdd.name)) {
            logAccess(LOG, request, "adds device " + deviceToAdd.name);
            Ticket result = handler.addDevice(deviceToAdd);
            if (result != null) {
                return result;
            } else {
                return new Ticket();
            }
        } else {
            Principals client = contextFactory.getPrincipals(request);
            LOG.warn(client.getReadableString()
                    + "tried to add invalid device " + deviceToAdd.name);
            return new Ticket();
        }
    }

    @GET
    @Produces("application/json")
    public Data[] getDevices(@Context HttpServletRequest request){
        logAccess(LOG, request, "gets devices");
        return handler.get(UserDeviceFactory.f);
    }

    @PUT
    @Path("/remove")
    @Consumes("application/json")
    public void removeDevice(@Context HttpServletRequest request,
                             UserDevice deviceToRemove){
        logAccess(LOG, request, "removes device " + deviceToRemove.name);
        handler.removeDevice(deviceToRemove);
    }


}
