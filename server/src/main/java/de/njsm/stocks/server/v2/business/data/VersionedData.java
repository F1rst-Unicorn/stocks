package de.njsm.stocks.server.v2.business.data;

public abstract class VersionedData extends Data {

    public int version;

    public VersionedData(int id, int version) {
        super(id);
        this.version = version;
    }

}
