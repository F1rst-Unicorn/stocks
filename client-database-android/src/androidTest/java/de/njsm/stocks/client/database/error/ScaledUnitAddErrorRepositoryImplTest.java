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
import de.njsm.stocks.client.business.entities.ScaledUnitAddErrorDetails;
import de.njsm.stocks.client.business.entities.ScaledUnitAddForm;
import de.njsm.stocks.client.database.StandardEntities;
import de.njsm.stocks.client.database.UnitDbEntity;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Collections.singletonList;

public class ScaledUnitAddErrorRepositoryImplTest extends AbstractErrorRepositoryImplTest {

    ErrorDetails recordError(StatusCodeException e) {
        UnitDbEntity unit = standardEntities.unitDbEntity();
        stocksDatabase.synchronisationDao().writeUnits(singletonList(unit));
        ScaledUnitAddForm form = ScaledUnitAddForm.create(BigDecimal.ONE, unit.id());
        errorRecorder.recordScaledUnitAddError(e, form);
        return ScaledUnitAddErrorDetails.create(form.scale(), form.unit(), unit.name(), unit.abbreviation());
    }

    @Override
    List<?> getErrorDetails() {
        return stocksDatabase.errorDao().getScaledUnitAdds();
    }
}
