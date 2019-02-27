package de.njsm.stocks.client.network.server;

import de.njsm.stocks.client.business.StatusCode;

import java.util.List;

public class ListResponse<T> extends DataResponse<List<T>> {

    public ListResponse(StatusCode status, List<T> data) {
        super(status, data);
    }
}
