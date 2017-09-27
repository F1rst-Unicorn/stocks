package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.Ticket;
import de.njsm.stocks.common.data.UserDevice;
import de.njsm.stocks.common.data.UserDeviceFactory;
import de.njsm.stocks.server.internal.Config;
import de.njsm.stocks.server.internal.auth.HttpsUserContextFactory;
import de.njsm.stocks.server.internal.auth.Principals;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/device")
public class DeviceEndpoint extends Endpoint {

    private static final Logger LOG = LogManager.getLogger(DeviceEndpoint.class);

    public DeviceEndpoint() {
    }

    public DeviceEndpoint(Config c) {
        super(c);
    }

    @PUT
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Ticket addDevice(@Context HttpServletRequest request,
                            UserDevice deviceToAdd) {

        if (HttpsUserContextFactory.isNameValid(deviceToAdd.name)) {
            logAccess(LOG, request, "adds device " + deviceToAdd.name);
            return handler.addDevice(deviceToAdd);

        } else {
            Principals client = c.getContextFactory().getPrincipals(request);
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
