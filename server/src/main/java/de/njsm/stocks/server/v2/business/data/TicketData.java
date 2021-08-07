package de.njsm.stocks.server.v2.business.data;

import com.fasterxml.jackson.annotation.JsonGetter;

public interface TicketData {

    @JsonGetter
    int deviceId();

    @JsonGetter
    String ticket();

    interface Builder<Builder, Data> {
        Builder deviceId(int v);

        Builder ticket(String v);

        Data build();
    }
}
