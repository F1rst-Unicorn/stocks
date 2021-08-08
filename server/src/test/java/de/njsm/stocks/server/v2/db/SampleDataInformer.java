package de.njsm.stocks.server.v2.db;

public interface SampleDataInformer {

    int getNumberOfEntities();

    default int getNextId() {
        return getNumberOfEntities() + 1;
    }
}
