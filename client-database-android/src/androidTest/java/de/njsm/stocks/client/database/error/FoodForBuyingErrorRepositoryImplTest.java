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
import de.njsm.stocks.client.business.entities.FoodForBuying;
import de.njsm.stocks.client.database.LocationDbEntity;
import de.njsm.stocks.client.database.ScaledUnitDbEntity;
import de.njsm.stocks.client.database.UnitDbEntity;

import java.util.List;

import static java.util.Collections.singletonList;

public class FoodForBuyingErrorRepositoryImplTest extends AbstractErrorRepositoryImplTest {

    ErrorDetails recordError(StatusCodeException e) {
        LocationDbEntity location = standardEntities.locationDbEntity();
        UnitDbEntity unit = standardEntities.unitDbEntity();
        ScaledUnitDbEntity scaledUnit = standardEntities.scaledUnitDbEntityBuilder()
                .unit(unit.id())
                .build();
        FoodForBuying form = FoodForBuying.create(randomnessProvider.getId("FoodForBuying"), 2, true);
        stocksDatabase.synchronisationDao().writeUnits(singletonList(unit));
        stocksDatabase.synchronisationDao().writeScaledUnits(singletonList(scaledUnit));
        stocksDatabase.synchronisationDao().writeLocations(singletonList(location));
        errorRecorder.recordFoodToBuyError(e, form);
        return form;
    }

    @Override
    List<?> getErrorDetails() {
        return stocksDatabase.errorDao().getFoodToBuy();
    }
}
