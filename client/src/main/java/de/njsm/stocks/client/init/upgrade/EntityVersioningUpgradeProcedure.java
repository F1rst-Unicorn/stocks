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

import java.util.ArrayList;
import java.util.List;

import static de.njsm.stocks.client.init.upgrade.Version.V_2_0_3;
import static de.njsm.stocks.client.init.upgrade.Version.V_3_0_0;

public class EntityVersioningUpgradeProcedure extends SqlUpgradeProcedure {


    public EntityVersioningUpgradeProcedure(DatabaseManager dbManager) {
        super(dbManager, V_2_0_3, V_3_0_0);
    }

    @Override
    protected List<String> getUpgradeSqlScript() {
        List<String> result = new ArrayList<>();
        result.add("ALTER TABLE Food ADD COLUMN `version` INT NOT NULL DEFAULT 0");
        result.add("ALTER TABLE User ADD COLUMN `version` INT NOT NULL DEFAULT 0");
        result.add("ALTER TABLE Location ADD COLUMN `version` INT NOT NULL DEFAULT 0");
        result.add("ALTER TABLE User_device ADD COLUMN `version` INT NOT NULL DEFAULT 0");
        result.add("ALTER TABLE Food_item ADD COLUMN `version` INT NOT NULL DEFAULT 0");
        return result;
    }

    @Override
    protected List<String> getDowngradeSqlScript() {
        List<String> result = new ArrayList<>();
        result.add("ALTER TABLE Food DROP COLUMN `version`");
        result.add("ALTER TABLE User DROP COLUMN `version`");
        result.add("ALTER TABLE Location DROP COLUMN `version`");
        result.add("ALTER TABLE User_device DROP COLUMN `version`");
        result.add("ALTER TABLE Food_item DROP COLUMN `version`");
        return result;
    }
}
