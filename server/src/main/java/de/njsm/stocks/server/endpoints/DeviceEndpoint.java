package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.server.data.Data;
import de.njsm.stocks.server.data.Ticket;
import de.njsm.stocks.server.data.UserDevice;
import de.njsm.stocks.server.data.UserDeviceFactory;
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

    @PUT
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Ticket addDevice(@Context HttpServletRequest request, UserDevice d) {
        Principals uc = c.getContextFactory().getPrincipals(request);
        LOG.info(uc.getUsername() + "@" + uc.getDeviceName() + " adds device " + d.name);

        return handler.addDevice(d);
    }

    @GET
    @Produces("application/json")
    public Data[] getDevices(@Context HttpServletRequest request){
        Principals uc = c.getContextFactory().getPrincipals(request);
        LOG.info(uc.getUsername() + "@" + uc.getDeviceName() + " gets devices");

        return handler.get(UserDeviceFactory.f);
    }

    @PUT
    @Path("/remove")
    @Consumes("application/json")
    public void removeDevice(@Context HttpServletRequest request, UserDevice d){
        Principals uc = c.getContextFactory().getPrincipals(request);
        LOG.info(uc.getUsername() + "@" + uc.getDeviceName() + " removes device");

        handler.removeDevice(d);
    }


}
