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

package de.njsm.stocks.client.database.error;

import de.njsm.stocks.client.business.StatusCodeException;
import de.njsm.stocks.client.business.entities.ErrorDetails;
import de.njsm.stocks.client.business.entities.ScaledUnitDeleteErrorDetails;
import de.njsm.stocks.client.business.entities.ScaledUnitForDeletion;
import de.njsm.stocks.client.database.ScaledUnitDbEntity;
import de.njsm.stocks.client.database.StandardEntities;
import de.njsm.stocks.client.database.UnitDbEntity;

import java.util.List;

import static java.util.Collections.singletonList;

public class ScaledUnitDeleteErrorRepositoryImplTest extends AbstractErrorRepositoryImplTest {

    ErrorDetails recordError(StatusCodeException e) {
        UnitDbEntity unit = StandardEntities.unitDbEntity();
        ScaledUnitDbEntity scaledUnit = StandardEntities.scaledUnitDbEntityBuilder().unit(unit.id()).build();
        ScaledUnitForDeletion data = ScaledUnitForDeletion.create(scaledUnit.id(), scaledUnit.version());
        ScaledUnitDeleteErrorDetails errorDetails = ScaledUnitDeleteErrorDetails.create(scaledUnit.id(), scaledUnit.scale(), unit.name(), unit.abbreviation());
        stocksDatabase.synchronisationDao().writeUnits(singletonList(unit));
        stocksDatabase.synchronisationDao().writeScaledUnits(singletonList(scaledUnit));
        errorRecorder.recordScaledUnitDeleteError(e, data);
        return errorDetails;
    }

    @Override
    List<?> getErrorDetails() {
        return stocksDatabase.errorDao().getScaledUnitDeletes();
    }
}
