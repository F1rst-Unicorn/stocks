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

import de.njsm.stocks.client.business.entities.event.EditedField;
import de.njsm.stocks.client.business.entities.event.FoodEditedEvent;
import de.njsm.stocks.client.ui.R;

import java.time.Period;
import java.util.function.Function;

class FoodExpirationOffsetDiffer extends PartialDiffGenerator<Period> {

    static FoodExpirationOffsetDiffer of(FoodEditedEvent event, Function<Integer, String> dictionary, SentenceObject object) {
        return new FoodExpirationOffsetDiffer(dictionary, event.expirationOffset(), object);
    }

    private FoodExpirationOffsetDiffer(Function<Integer, String> dictionary, EditedField<Period> editedField, SentenceObject object) {
        super(dictionary, editedField, object);
    }

    @Override
    int getStringId() {
        return R.string.event_food_expiration_offset_set;
    }

    @Override
    Object[] getFormatArguments() {
        return new Object[] {
                getObject().getGenitive(),
                get().current().getDays()
        };
    }
}
