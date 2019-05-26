/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
