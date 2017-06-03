package de.njsm.stocks.client.init.upgrade;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InitialisationException;
import de.njsm.stocks.client.storage.DatabaseManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class UpgradeManager {

    private static final Logger LOG = LogManager.getLogger(UpgradeManager.class);

    private final DatabaseManager dbManager;

    private final UpgradeRegistry registry;

    private Version dbVersionAtStartup;

    public UpgradeManager(DatabaseManager dbManager, UpgradeRegistry registry) {
        this.dbManager = dbManager;
        this.registry = registry;
    }

    public boolean needsUpgrade() throws InitialisationException {
        try {
            Version dbVersion = dbManager.getDbVersion();
            dbVersionAtStartup = dbVersion;
            LOG.info("DB version is " + dbVersion);
            LOG.info("Software version is " + Version.CURRENT);
            return dbVersion.compareTo(Version.CURRENT) == -1;
        } catch (DatabaseException e) {
            throw new InitialisationException("Could not read current version from DB", e);
        }
    }

    public void upgrade() throws InitialisationException {
        List<Upgrader> upgraders = registry.getUpgraders(dbVersionAtStartup, Version.CURRENT);
        for (Upgrader upgrader : upgraders) {
            upgrader.upgrade();
        }
    }
}
