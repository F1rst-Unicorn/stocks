package de.njsm.stocks.client.init.upgrade;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InitialisationException;
import de.njsm.stocks.client.storage.DatabaseManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public abstract class SqlUpgradeProcedure extends UpgradeProcedure {

    private static final Logger LOG = LogManager.getLogger(SqlUpgradeProcedure.class);

    public SqlUpgradeProcedure(DatabaseManager dbManager, Version from, Version to) {
        super(dbManager, from, to);
    }

    @Override
    public final void upgrade() throws InitialisationException {
        LOG.info("Running upgrade from " + getBaseVersion() + " to " + getTargetVersion());

        List<String> commands = getUpgradeSqlScript();
        commands.add("UPDATE Config SET value='" + getTargetVersion() + "' WHERE key='db.version'");

        try {
            dbManager.runSqlScript(commands);
        } catch (DatabaseException e) {
            throw new InitialisationException("Upgrade failed", e);
        }

        LOG.info("Upgrade successful");
    }

    @Override
    public final void downgrade() throws InitialisationException {
        LOG.info("Running downgrade from " + getTargetVersion() + " to " + getBaseVersion());

        List<String> commands = getUpgradeSqlScript();
        commands.add(0, "UPDATE Config SET value='" + getBaseVersion() + "' WHERE key='db.version'");

        try {
            dbManager.runSqlScript(commands);
        } catch (DatabaseException e) {
            throw new InitialisationException("Upgrade failed", e);
        }

        LOG.info("Upgrade successful");
    }

    protected abstract List<String> getUpgradeSqlScript();

    protected abstract List<String> getDowngradeSqlScript();
}
