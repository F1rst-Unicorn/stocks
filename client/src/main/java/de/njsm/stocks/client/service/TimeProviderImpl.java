package de.njsm.stocks.client.service;

public class TimeProviderImpl implements TimeProvider {

    @Override
    public long getTime() {
        return System.currentTimeMillis();
    }

}
