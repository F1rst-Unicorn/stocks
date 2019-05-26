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
