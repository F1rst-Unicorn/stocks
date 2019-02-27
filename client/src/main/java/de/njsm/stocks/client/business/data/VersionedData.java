package de.njsm.stocks.client.business.data;

public abstract class VersionedData extends Data {

    public int version;

    public VersionedData() {
    }

    public VersionedData(int id, int version) {
        super(id);
        this.version = version;
    }

}
