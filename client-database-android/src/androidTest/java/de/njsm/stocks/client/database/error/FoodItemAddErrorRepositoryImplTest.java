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
import de.njsm.stocks.client.business.entities.FoodItemAddErrorDetails;
import de.njsm.stocks.client.business.entities.FoodItemForm;
import de.njsm.stocks.client.database.FoodDbEntity;
import de.njsm.stocks.client.database.LocationDbEntity;
import de.njsm.stocks.client.database.ScaledUnitDbEntity;
import de.njsm.stocks.client.database.UnitDbEntity;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.singletonList;

public class FoodItemAddErrorRepositoryImplTest extends AbstractErrorRepositoryImplTest {

    @Override
    ErrorDetails recordError(StatusCodeException e) {
        UnitDbEntity unit = standardEntities.unitDbEntity();
        stocksDatabase.synchronisationDao().writeUnits(singletonList(unit));
        ScaledUnitDbEntity scaledUnit = standardEntities.scaledUnitDbEntityBuilder().unit(unit.id()).build();
        stocksDatabase.synchronisationDao().writeScaledUnits(singletonList(scaledUnit));
        LocationDbEntity location = standardEntities.locationDbEntity();
        stocksDatabase.synchronisationDao().writeLocations(singletonList(location));
        FoodDbEntity food = standardEntities.foodDbEntity();
        stocksDatabase.synchronisationDao().writeFood(singletonList(food));
        FoodItemForm form = FoodItemForm.create(LocalDate.ofEpochDay(2), food.id(), location.id(), scaledUnit.id());
        errorRecorder.recordFoodItemAddError(e, form);
        return FoodItemAddErrorDetails.create(
                form.eatBy(),
                form.ofType(),
                form.storedIn(),
                form.unit(),
                FoodItemAddErrorDetails.Unit.create(scaledUnit.scale(), unit.abbreviation()),
                food.name(),
                location.name()
        );
    }

    @Override
    List<?> getErrorDetails() {
        return stocksDatabase.errorDao().getFoodItemAdds();
    }
}
