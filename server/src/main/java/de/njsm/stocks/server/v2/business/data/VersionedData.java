package de.njsm.stocks.server.v2.business.data;

public abstract class VersionedData extends Data {

    public int version;

    public VersionedData() {
    }

    public VersionedData(int id) {
        super(id);
        this.version = 0;
    }

    public VersionedData(int id, int version) {
        super(id);
        this.version = version;
    }

}
