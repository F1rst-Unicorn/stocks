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
