package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.server.data.Data;
import de.njsm.stocks.server.data.Ticket;
import de.njsm.stocks.server.data.UserDevice;
import de.njsm.stocks.server.data.UserDeviceFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.logging.Level;

@Path("/device")
public class DeviceEndpoint extends Endpoint {

    @PUT
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Ticket addDevice(UserDevice d) {
        c.getLog().log(Level.INFO, "DeviceEndpoint: Add device " + d.name);
        return handler.addDevice(d);
    }

    @GET
    @Produces("application/json")
    public Data[] getDevices(){
        c.getLog().log(Level.INFO, "DeviceEndpoint: Get devices");
        return handler.get(UserDeviceFactory.f);
    }

    @PUT
    @Path("/remove")
    @Consumes("application/json")
    public void removeDevice(UserDevice d){
        c.getLog().log(Level.INFO, "DeviceEndpoint: Removing device " + d.name);
        handler.removeDevice(d);
    }


}
