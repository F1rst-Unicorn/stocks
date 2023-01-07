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

package de.njsm.stocks.client.database;

import de.njsm.stocks.client.business.RecipeAddRepository;
import de.njsm.stocks.client.business.ScaledUnitRepository;
import de.njsm.stocks.client.business.entities.FoodForSelection;
import de.njsm.stocks.client.business.entities.ScaledUnitForSelection;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

class RecipeAddRepositoryImpl implements RecipeAddRepository {

    private final FoodDao foodDao;

    private final ScaledUnitRepository scaledUnitRepository;

    @Inject
    RecipeAddRepositoryImpl(FoodDao foodDao, ScaledUnitRepository scaledUnitRepository) {
        this.foodDao = foodDao;
        this.scaledUnitRepository = scaledUnitRepository;
    }

    @Override
    public Observable<List<FoodForSelection>> getFood() {
        return foodDao.getForSelection();
    }

    @Override
    public Observable<List<ScaledUnitForSelection>> getUnits() {
        return scaledUnitRepository.getScaledUnitsForSelection();
    }
}
