package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.TicketAuthoriser;
import de.njsm.stocks.server.v2.business.data.ClientTicket;
import de.njsm.stocks.server.v2.web.data.DataResponse;
import fj.data.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("v2/auth")
public class RegistrationEndpoint extends de.njsm.stocks.server.v2.web.Endpoint {

    private static final Logger LOG = LogManager.getLogger(RegistrationEndpoint.class);

    private TicketAuthoriser authoriser;

    public RegistrationEndpoint(TicketAuthoriser authoriser) {
        this.authoriser = authoriser;
    }

    /**
     * Get a new user certificate
     * @return A response containing the new user certificate
     */
    @POST
    @Path("newuser")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public DataResponse<String> getNewCertificate(@FormParam("device") int device,
                                                  @FormParam("token") String token,
                                                  @FormParam("csr") String csr){

        LOG.info("Got new certificate request for device id " + device);

        Validation<StatusCode, String> response = authoriser.handleTicket(new ClientTicket(device, token, csr));

        return new DataResponse<>(response);
    }

}
