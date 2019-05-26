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
            dbVersionAtStartup = dbManager.getDbVersion();

            LOG.info("DB version is " + dbVersionAtStartup);
            LOG.info("Software version is " + Version.CURRENT);

            return dbVersionAtStartup.compareTo(Version.CURRENT) == -1;
        } catch (DatabaseException e) {
            throw new InitialisationException("Could not read current version from DB", e);
        }
    }

    public void upgrade() throws InitialisationException {
        List<UpgradeProcedure> upgradeProcedures = registry.getUpgradeProcedures(dbVersionAtStartup, Version.CURRENT);
        for (UpgradeProcedure upgradeProcedure : upgradeProcedures) {
            upgradeProcedure.upgrade();
        }
    }
}
