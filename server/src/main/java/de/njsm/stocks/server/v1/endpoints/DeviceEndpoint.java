package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.Ticket;
import de.njsm.stocks.common.data.UserDevice;
import de.njsm.stocks.server.v1.internal.business.UserContextFactory;
import de.njsm.stocks.server.v1.internal.business.DevicesManager;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/device")
public class DeviceEndpoint extends Endpoint {

    private static final Logger LOG = LogManager.getLogger(DeviceEndpoint.class);

    private DevicesManager devicesManager;

    public DeviceEndpoint(DevicesManager devicesManager,
                          DatabaseHandler handler,
                          UserContextFactory contextFactory) {
        super(handler, contextFactory);
        this.devicesManager = devicesManager;
    }

    @PUT
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Ticket addDevice(@Context HttpServletRequest request,
                            UserDevice deviceToAdd) {

        logAccess(LOG, request, "adds device " + deviceToAdd.name);
        return devicesManager.addDevice(deviceToAdd);
    }

    @GET
    @Produces("application/json")
    public Data[] getDevices(@Context HttpServletRequest request){
        logAccess(LOG, request, "gets devices");
        return devicesManager.getDevices();
    }

    @PUT
    @Path("/remove")
    @Consumes("application/json")
    public void removeDevice(@Context HttpServletRequest request,
                             UserDevice deviceToRemove){
        logAccess(LOG, request, "removes device " + deviceToRemove.name);
        devicesManager.removeDevice(deviceToRemove);
    }


}
