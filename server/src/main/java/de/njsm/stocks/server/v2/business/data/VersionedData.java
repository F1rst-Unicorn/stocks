package de.njsm.stocks.server.v2.business.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VersionedData extends Data {

    public int version;

    public VersionedData(int id, int version) {
        super(id);
        this.version = version;
    }

    public VersionedData() {
    }

}
