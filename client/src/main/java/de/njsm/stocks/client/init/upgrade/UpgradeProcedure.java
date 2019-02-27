package de.njsm.stocks.client.init.upgrade;

import de.njsm.stocks.client.exceptions.InitialisationException;
import de.njsm.stocks.client.storage.DatabaseManager;

public abstract class UpgradeProcedure {

    protected final DatabaseManager dbManager;

    private Version baseVersion;

    private Version targetVersion;

    public UpgradeProcedure(DatabaseManager dbManager,
                            Version from,
                            Version to) {
        this.dbManager = dbManager;
        this.baseVersion = from;
        this.targetVersion = to;
    }

    public abstract void upgrade() throws InitialisationException;

    public abstract void downgrade() throws InitialisationException;

    public Version getBaseVersion() {
        return baseVersion;
    }

    public Version getTargetVersion() {
        return targetVersion;
    }
}
