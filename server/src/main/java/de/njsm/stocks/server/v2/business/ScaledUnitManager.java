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

package de.njsm.stocks.server.v2.business;


import de.njsm.stocks.common.api.ScaledUnit;
import de.njsm.stocks.common.api.ScaledUnitForDeletion;
import de.njsm.stocks.common.api.ScaledUnitForEditing;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.v2.db.ScaledUnitHandler;
import de.njsm.stocks.server.v2.db.jooq.tables.records.ScaledUnitRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

@Service
@RequestScope
public class ScaledUnitManager extends BusinessObject<ScaledUnitRecord, ScaledUnit>
        implements BusinessGettable<ScaledUnitRecord, ScaledUnit>,
                   BusinessAddable<ScaledUnitRecord, ScaledUnit>,
                   BusinessDeletable<ScaledUnitForDeletion, ScaledUnit> {

    private final ScaledUnitHandler dbHandler;

    public ScaledUnitManager(ScaledUnitHandler dbHandler) {
        super(dbHandler);
        this.dbHandler = dbHandler;
    }

    public StatusCode edit(ScaledUnitForEditing data) {
        return runOperation(() -> dbHandler.edit(data));
    }

    public StatusCode delete(ScaledUnitForDeletion ScaledUnit) {
        return runOperation(() -> {
            var currentScaledUnitsResult = dbHandler.countCurrent();
            if (currentScaledUnitsResult.isFail())
                return currentScaledUnitsResult.fail();

            var currentScaledUnits = currentScaledUnitsResult.success();
            if (currentScaledUnits == 1)
                return StatusCode.ACCESS_DENIED;

            return dbHandler.delete(ScaledUnit);
        });
    }
}
