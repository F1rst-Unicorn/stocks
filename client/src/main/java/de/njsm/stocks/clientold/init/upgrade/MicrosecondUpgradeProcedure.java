/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.clientold.init.upgrade;

import de.njsm.stocks.clientold.exceptions.InitialisationException;
import de.njsm.stocks.clientold.storage.DatabaseManager;
import de.njsm.stocks.clientold.config.Configuration;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static de.njsm.stocks.clientold.init.upgrade.Version.V_3_0_0;
import static de.njsm.stocks.clientold.init.upgrade.Version.V_3_0_1;

public class MicrosecondUpgradeProcedure extends UpgradeProcedure {


    public MicrosecondUpgradeProcedure(DatabaseManager dbManager) {
        super(dbManager, V_3_0_0, V_3_0_1);
    }

    @Override
    public void upgrade() throws InitialisationException {
        try {
            File input = new File(Configuration.SYSTEM_DB_PATH);
            File output = new File(Configuration.DB_PATH);
            FileUtils.copyFile(input, output);
        } catch (IOException e) {
            throw new InitialisationException("DB upgrade failed", e);
        }
    }

    @Override
    public void downgrade() throws InitialisationException {
        throw new InitialisationException("downgrading not possible");
    }
}
