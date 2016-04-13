package de.njsm.stocks.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;

@Path("/test")
public class Test {

    @GET
    @Produces("text/plain")
    public String getMessage() {
        return "Hello World";
    }

}
