package de.njsm.stocks.android.db.entities;

import androidx.room.ColumnInfo;

public abstract class VersionedData extends Data {

    @ColumnInfo(name = "version")
    public int version;

    public VersionedData() {
    }

    public VersionedData(int id, int version) {
        super(id);
        this.version = version;
    }

}
