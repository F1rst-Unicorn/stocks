package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.web.data.DataResponse;
import fj.data.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("v2/auth")
public class RegistrationEndpoint extends de.njsm.stocks.server.v2.web.Endpoint {

    private static final Logger LOG = LogManager.getLogger(RegistrationEndpoint.class);

    public RegistrationEndpoint() {
    }

    /**
     * Get a new user certificate
     * @return A response containing the new user certificate
     */
    @POST
    @Path("newuser")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public DataResponse<String> getNewCertificate(@Context HttpServletRequest request,
                                                  @FormParam("device") int device,
                                                  @FormParam("token") String token,
                                                  @FormParam("csr") String csr){

        LOG.info("Got new certificate request for device id " + device);
        return new DataResponse<>(Validation.fail(StatusCode.ACCESS_DENIED));
    }

}
