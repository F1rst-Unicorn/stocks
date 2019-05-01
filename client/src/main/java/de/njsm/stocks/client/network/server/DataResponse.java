package de.njsm.stocks.client.network.server;

import de.njsm.stocks.client.business.StatusCode;

public class DataResponse<T> extends Response {

    public T data;

    public DataResponse(StatusCode status, T data) {
        super(status);
        this.data = data;
    }

    public DataResponse() {
        super();
    }
}
