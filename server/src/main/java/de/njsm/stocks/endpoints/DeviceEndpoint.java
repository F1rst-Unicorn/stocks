package de.njsm.stocks.endpoints;

import de.njsm.stocks.data.Ticket;
import de.njsm.stocks.data.UserDevice;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/device")
public class DeviceEndpoint extends Endpoint {

    @PUT
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Ticket addDevice(UserDevice d) {
        c.getLog().log(Level.INFO, "DeviceEndpoint: Add device " + d.name);
        try {
            return handler.addDevice(d);
        } catch (SQLException e) {
            c.getLog().log(Level.SEVERE, "DeviceEndpoint: Failed to add devices " + e.getMessage());
            return new Ticket();
        }
    }

    @GET
    @Produces("application/json")
    public UserDevice[] getDevices(){
        c.getLog().log(Level.INFO, "DeviceEndpoint: Get devices");
        try {
            return handler.getDevices();
        } catch (SQLException e){
            c.getLog().log(Level.SEVERE, "DeviceEndpoint: Failed to get devices: " + e.getMessage());
        }
        return null;
    }

    @PUT
    @Path("/remove")
    @Consumes("application/json")
    public void removeDevice(UserDevice d){
        c.getLog().log(Level.INFO, "DeviceEndpoint: Removing device " + d);
        try {
            handler.removeDevice(d.id);
        } catch (SQLException e){
            c.getLog().log(Level.SEVERE, "DeviceEndpoint: Failed to remove device" + e.getMessage());
        }
    }


}
