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

package de.njsm.stocks.client.business.entities.conflict;

import com.google.auto.value.AutoValue;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.Unit;

@AutoValue
public abstract class UnitEditConflictData implements Id<Unit> {

    public abstract int errorId();

    public abstract int originalVersion();

    public abstract ConflictData<String> name();

    public abstract ConflictData<String> abbreviation();

    public boolean hasAnyConflict() {
        return name().needsHandling() || abbreviation().needsHandling();
    }

    public boolean hasNoConflict() {
        return !hasAnyConflict();
    }

    public static UnitEditConflictData create(
            int errorId,
            int id,
            int originalVersion,
            String originalName,
            String remoteName,
            String localName,
            String originalAbbreviation,
            String remoteAbbreviation,
            String localAbbreviation) {
        return new AutoValue_UnitEditConflictData(id, errorId, originalVersion,
                ConflictData.create(originalName, remoteName, localName),
                ConflictData.create(originalAbbreviation, remoteAbbreviation, localAbbreviation));
    }
}
