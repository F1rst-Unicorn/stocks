/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2021  The stocks developers
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
 */

package de.njsm.stocks.client.databind.event;

import de.njsm.stocks.client.business.entities.UnitAmount;
import de.njsm.stocks.client.business.entities.event.EditedField;
import de.njsm.stocks.client.business.entities.event.FoodEditedEvent;
import de.njsm.stocks.client.presenter.UnitAmountRenderStrategy;
import de.njsm.stocks.client.ui.R;

import java.util.function.Function;

public class FoodUnitDiffer extends PartialDiffGenerator<UnitAmount> {

    private final UnitAmountRenderStrategy unitAmountRenderStrategy;

    public static FoodUnitDiffer of(FoodEditedEvent event, Function<Integer, String> dictionary, SentenceObject object, UnitAmountRenderStrategy unitAmountRenderStrategy) {
        return new FoodUnitDiffer(dictionary, event.unit(), object, unitAmountRenderStrategy);
    }

    private FoodUnitDiffer(Function<Integer, String> dictionary, EditedField<UnitAmount> editedField, SentenceObject object, UnitAmountRenderStrategy unitAmountRenderStrategy) {
        super(dictionary, editedField, object);
        this.unitAmountRenderStrategy = unitAmountRenderStrategy;
    }

    @Override
    protected int getStringId() {
        return R.string.event_food_store_unit_changed;
    }

    @Override
    protected Object[] getFormatArguments() {
        return new Object[] {
                getObject().getGenitive(),
                unitAmountRenderStrategy.render(get().former()),
                unitAmountRenderStrategy.render(get().current())
        };
    }
}
