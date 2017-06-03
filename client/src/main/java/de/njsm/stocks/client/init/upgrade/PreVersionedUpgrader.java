package de.njsm.stocks.client.init.upgrade;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InitialisationException;
import de.njsm.stocks.client.storage.DatabaseManager;

import java.util.LinkedList;
import java.util.List;

public class PreVersionedUpgrader extends Upgrader {

    public PreVersionedUpgrader(DatabaseManager dbManager) {
        super(dbManager, Version.PRE_VERSIONED, Version.V_0_5_0);
    }

    @Override
    public void upgrade() throws InitialisationException {
        List<String> commands = new LinkedList<>();
        commands.add("CREATE TABLE Config ( " +
                "`key` varchar(100) NOT NULL UNIQUE, " +
                "`value` varchar(100) NOT NULL, " +
                "PRIMARY KEY (`key`) " +
                ")");
        commands.add("INSERT INTO Config (key, value) VALUES " +
                "('db.version', '" + Version.V_0_5_0 + "'");
        try {
            dbManager.runSqlScript(commands);
        } catch (DatabaseException e) {
            throw new InitialisationException("Upgrade failed", e);
        }
    }
}
