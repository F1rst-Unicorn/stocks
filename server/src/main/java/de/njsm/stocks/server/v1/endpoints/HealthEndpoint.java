package de.njsm.stocks.server.v1.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/health")
public class HealthEndpoint {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getStatus() {
        return "System is healthy";
    }
}
