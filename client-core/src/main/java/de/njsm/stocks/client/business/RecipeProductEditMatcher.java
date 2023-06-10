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

import de.njsm.stocks.client.business.entities.*;

import java.util.List;
import java.util.function.Function;

class RecipeProductEditMatcher extends RecipeFoodEditMatcher<
        RecipeProductEditData,
        RecipeProductToAdd,
        RecipeProductDeleteNetworkData,
        RecipeProductEditFormData,
        RecipeProductEditNetworkData> {

    private final Function<Id<RecipeProduct>, Versionable<RecipeProduct>> versionProvider;

    RecipeProductEditMatcher(List<RecipeProductEditData> present, List<RecipeProductEditFormData> form, Function<Id<RecipeProduct>, Versionable<RecipeProduct>> versionProvider) {
        super(present, form);
        this.versionProvider = versionProvider;
    }

    @Override
    RecipeProductToAdd createToAdd(RecipeProductEditFormData v) {
        return RecipeProductToAdd.create(v.amount(), v.product(), v.unit());
    }

    @Override
    RecipeProductDeleteNetworkData createToDelete(RecipeProductEditData v) {
        return RecipeProductDeleteNetworkData.create(v.id(), versionProvider.apply(v).version());
    }

    @Override
    RecipeProductEditNetworkData createToEdit(RecipeProductEditData presentItem, RecipeProductEditFormData formItem) {
        return RecipeProductEditNetworkData.create(
                presentItem.id(),
                versionProvider.apply(formItem).version(),
                formItem.amount(),
                formItem.unit(),
                formItem.product()
        );
    }

    @Override
    Rating rate(RecipeProductEditData present, RecipeProductEditFormData form) {
        if (present.product().id() == form.product().id())
            if (present.unit().id() == form.unit().id())
                if (present.amount() == form.amount())
                    if (present.id() == form.id())
                        return Rating.PERFECT;
                    else
                        return Rating.EQUAL;
                else
                    return Rating.AMOUNT_CHANGED;
            else
                return Rating.UNIT_CHANGED;
        else
            return Rating.UNRELATED;
    }
}
