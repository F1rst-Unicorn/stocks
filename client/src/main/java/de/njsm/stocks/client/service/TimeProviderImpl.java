package de.njsm.stocks.client.service;

import java.util.Date;
import java.util.TimeZone;

public class TimeProviderImpl implements TimeProvider {

    @Override
    public long getTime() {
        return System.currentTimeMillis();
    }

    public static Date convertUtcToLocaltime(Date utcDate) {
        return new Date(utcDate.getTime()
                - TimeZone.getDefault().getOffset(utcDate.getTime()));
    }

}
