package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.UserManager;
import de.njsm.stocks.server.v2.business.data.User;
import de.njsm.stocks.server.v2.web.data.DataResponse;
import de.njsm.stocks.server.v2.web.data.ListResponse;
import de.njsm.stocks.server.v2.web.data.Response;
import fj.data.Validation;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.List;

@Path("/v2/user")
public class UserEndpoint extends Endpoint {

    private UserManager userManager;

    public UserEndpoint(UserManager userManager) {
        this.userManager = userManager;
    }

    @PUT
    @Produces("application/json")
    public Response putUser(@QueryParam("name") String name) {

        if (isValid(name, "name")) {
            StatusCode result = userManager.addUser(new User(name));
            return new Response(result);

        } else {
            return new DataResponse<>(Validation.fail(StatusCode.INVALID_ARGUMENT));
        }
    }

    @GET
    @Produces("application/json")
    public ListResponse<User> getUsers() {
        Validation<StatusCode, List<User>> list = userManager.get();
        return new ListResponse<>(list);
    }

    @DELETE
    @Produces("application/json")
    public Response deleteUser(@Context HttpServletRequest request,
                           @QueryParam("id") int id,
                           @QueryParam("version") int version) {

        if (isValid(id, "id") &&
                isValidVersion(version, "version")) {
            StatusCode result = userManager.deleteUser(new User(id, version), getPrincipals(request));
            return new Response(result);
        } else {
            return new DataResponse<>(Validation.fail(StatusCode.INVALID_ARGUMENT));
        }

    }

}
