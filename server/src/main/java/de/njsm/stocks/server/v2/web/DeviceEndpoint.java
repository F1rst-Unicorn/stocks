package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.v2.business.DeviceManager;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.ClientTicket;
import de.njsm.stocks.server.v2.business.data.UserDevice;
import de.njsm.stocks.server.v2.web.data.DataResponse;
import de.njsm.stocks.server.v2.web.data.ListResponse;
import de.njsm.stocks.server.v2.web.data.Response;
import fj.data.Validation;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("v2/device")
public class DeviceEndpoint extends Endpoint {

    private DeviceManager deviceManager;

    public DeviceEndpoint(DeviceManager deviceManager) {
        this.deviceManager = deviceManager;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public DataResponse<ClientTicket> putDevice(@QueryParam("name") String name,
                                                @QueryParam("belongsTo") int userId) {
        if (isValid(name, "name") &&
                isValid(userId, "userId")) {

            Validation<StatusCode, ClientTicket> result = deviceManager.addDevice(new UserDevice(name, userId));
            return new DataResponse<>(result);
        } else {
            return new DataResponse<>(Validation.fail(StatusCode.INVALID_ARGUMENT));
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ListResponse<UserDevice> getDevices() {
        Validation<StatusCode, List<UserDevice>> result = deviceManager.get();
        return new ListResponse<>(result);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteDevice(@Context HttpServletRequest request,
                                 @QueryParam("id") int id,
                                 @QueryParam("version") int version) {
        if (isValid(id, "id") &&
                isValid(version, "version")) {

            StatusCode result = deviceManager.removeDevice(new UserDevice(id, version), getPrincipals(request));
            return new Response(result);
        } else {
            return new DataResponse<>(Validation.fail(StatusCode.INVALID_ARGUMENT));
        }
    }

    @DELETE
    @Path("/revoke")
    @Produces(MediaType.APPLICATION_JSON)
    public Response revokeDevice(@Context HttpServletRequest request,
                                 @QueryParam("id") int id,
                                 @QueryParam("version") int version) {
        if (isValid(id, "id") &&
                isValid(version, "version")) {

            StatusCode result = deviceManager.revokeDevice(new UserDevice(id, version));
            return new Response(result);
        } else {
            return new DataResponse<>(Validation.fail(StatusCode.INVALID_ARGUMENT));
        }
    }
}
