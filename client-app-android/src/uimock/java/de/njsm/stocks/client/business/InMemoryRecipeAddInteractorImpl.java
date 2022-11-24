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

package de.njsm.stocks.client.business;

import de.njsm.stocks.client.business.entities.FoodForSelection;
import de.njsm.stocks.client.business.entities.RecipeAddData;
import de.njsm.stocks.client.business.entities.RecipeAddForm;
import de.njsm.stocks.client.business.entities.ScaledUnitForSelection;
import io.reactivex.rxjava3.core.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

public class InMemoryRecipeAddInteractorImpl implements RecipeAddInteractor {

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryRecipeAddInteractorImpl.class);

    @Inject
    InMemoryRecipeAddInteractorImpl() {
    }

    @Override
    public Observable<RecipeAddData> getData() {
        return Observable.just(RecipeAddData.create(
                List.of(FoodForSelection.create(2, "Banana"),
                        FoodForSelection.create(5, "Flour"),
                        FoodForSelection.create(14, "Water")),
                List.of(ScaledUnitForSelection.create(1, "g", BigDecimal.ONE),
                        ScaledUnitForSelection.create(3, "g", BigDecimal.valueOf(1000)),
                        ScaledUnitForSelection.create(4, "l", BigDecimal.ONE),
                        ScaledUnitForSelection.create(6, "l", BigDecimal.valueOf(0.5)))
        ));
    }

    @Override
    public void add(RecipeAddForm form) {
        LOG.debug(form.toString());
    }
}
