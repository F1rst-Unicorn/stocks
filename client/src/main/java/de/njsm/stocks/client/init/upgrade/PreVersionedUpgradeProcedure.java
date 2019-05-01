package de.njsm.stocks.client.init.upgrade;

import de.njsm.stocks.client.storage.DatabaseManager;

import java.util.LinkedList;
import java.util.List;

public class PreVersionedUpgradeProcedure extends SqlUpgradeProcedure {

    public PreVersionedUpgradeProcedure(DatabaseManager dbManager) {
        super(dbManager, Version.PRE_VERSIONED, Version.V_0_5_0);
    }

    @Override
    public List<String> getUpgradeSqlScript() {
        List<String> commands = new LinkedList<>();
        commands.add("CREATE TABLE Config ( " +
                "`key` varchar(100) NOT NULL UNIQUE, " +
                "`value` varchar(100) NOT NULL, " +
                "PRIMARY KEY (`key`) " +
                ")");
        commands.add("INSERT INTO Config (key, value) VALUES " +
                "('db.version', '" + Version.V_0_5_0 + "')");
        return commands;
    }

    @Override
    public List<String> getDowngradeSqlScript() {
        List<String> commands = new LinkedList<>();
        commands.add("DROP TABLE Config");
        return commands;
    }
}
