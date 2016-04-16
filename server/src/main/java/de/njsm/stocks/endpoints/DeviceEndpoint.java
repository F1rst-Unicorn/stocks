package de.njsm.stocks.endpoints;

import de.njsm.stocks.data.UserDevice;

import javax.ws.rs.*;
import java.sql.SQLException;

@Path("/device")
public class DeviceEndpoint extends Endpoint {

    @GET
    @Produces("application/json")
    public UserDevice[] getDevices(){
        try {
            return handler.getDevices();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @PUT
    @Consumes("application/json")
    public void removeDevice(UserDevice d){
        try {
            handler.removeDevice(d.id);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }


}
