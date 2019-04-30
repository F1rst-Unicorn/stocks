package de.njsm.stocks.client.network.server;

import de.njsm.stocks.client.business.StatusCode;

public class ListResponse<T> extends DataResponse<T[]> {

    public ListResponse(StatusCode status, T[] data) {
        super(status, data);
    }

    public ListResponse() {
        super();
    }
}
