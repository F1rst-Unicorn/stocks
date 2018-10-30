package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.server.v1.internal.data.Data;
import de.njsm.stocks.server.v1.internal.data.Ticket;
import de.njsm.stocks.server.v1.internal.data.UserDevice;
import de.njsm.stocks.server.v1.internal.business.DevicesManager;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/device")
public class DeviceEndpoint extends Endpoint {

    private DevicesManager devicesManager;

    public DeviceEndpoint(DevicesManager devicesManager,
                          DatabaseHandler handler) {
        super(handler);
        this.devicesManager = devicesManager;
    }

    @PUT
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Ticket addDevice(@Context HttpServletRequest request,
                            UserDevice deviceToAdd) {
        deviceToAdd.id = 0;
        return devicesManager.addDevice(deviceToAdd);
    }

    @GET
    @Produces("application/json")
    public Data[] getDevices(@Context HttpServletRequest request){
        return devicesManager.getDevices();
    }

    @PUT
    @Path("/remove")
    @Consumes("application/json")
    public void removeDevice(@Context HttpServletRequest request,
                             UserDevice deviceToRemove){
        devicesManager.removeDevice(deviceToRemove);
    }


}
