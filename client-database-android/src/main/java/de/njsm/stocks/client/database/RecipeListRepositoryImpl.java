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

import com.google.auto.value.AutoValue;
import de.njsm.stocks.client.business.ListRegrouper;
import de.njsm.stocks.client.business.RecipeListRepository;
import de.njsm.stocks.client.business.entities.RecipeForListingBaseData;
import de.njsm.stocks.client.business.entities.RecipeIngredientAmount;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

class RecipeListRepositoryImpl implements RecipeListRepository {

    private final RecipeDao recipeDao;

    @Inject
    RecipeListRepositoryImpl(RecipeDao recipeDao) {
        this.recipeDao = recipeDao;
    }

    @Override
    public Observable<List<RecipeForListingBaseData>> get() {
        return recipeDao.getRecipes();
    }

    @Override
    public Observable<List<RecipeIngredientAmount>> getIngredients() {
        return recipeDao.getIngredientsRequiredAmount().zipWith(recipeDao.getIngredientsPresentAmounts(), (required, present) -> {
            List<RecipeIngredientAmount> result = new ArrayList<>();
            var regrouper = new ListRegrouper<>(
                    new ListRegrouper.Group<>(required.iterator(), RecipeIngredientAmountBaseData::ingredient),
                    new ListRegrouper.Group<>(present.iterator(), RecipeIngredientAmountBaseData::ingredient),
                    (requiredItem, presentItems) -> result.add(RecipeIngredientAmount.create(requiredItem.recipe(), requiredItem.intoAmount(),
                            presentItems.stream().map(RecipeIngredientAmountBaseData::intoAmount).collect(toList()))
                    ));
            regrouper.execute();
            return result;
        });
    }

    @AutoValue
    abstract static class RecipeIngredientAmountBaseData {

        public abstract int recipe();

        public abstract int ingredient();

        public abstract int unit();

        public abstract BigDecimal scale();

        public abstract int amount();

        public RecipeIngredientAmount.Amount intoAmount() {
            return RecipeIngredientAmount.Amount.create(unit(), scale(), amount());
        }

        public static RecipeIngredientAmountBaseData create(int recipe, int ingredient, int unit, BigDecimal scale, int amount) {
            return new AutoValue_RecipeListRepositoryImpl_RecipeIngredientAmountBaseData(recipe, ingredient, unit, scale, amount);
        }
    }
}
