package de.njsm.stocks.client.network.server;

import de.njsm.stocks.client.business.StatusCode;

public class Response {

    public StatusCode status;

    public Response(StatusCode status) {
        this.status = status;
    }
}
