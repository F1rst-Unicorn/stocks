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
import de.njsm.stocks.client.business.RecipeDetailRepository;
import de.njsm.stocks.client.business.entities.*;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

class RecipeDetailRepositoryImpl implements RecipeDetailRepository {

    private final RecipeDao recipeDao;

    @Inject
    RecipeDetailRepositoryImpl(RecipeDao recipeDao) {
        this.recipeDao = recipeDao;
    }

    @Override
    public Observable<RecipeForDetailsBaseData> get(Id<Recipe> recipeId) {
        return recipeDao.getRecipe(recipeId.id());
    }

    @Override
    public Observable<List<RecipeIngredientForDetails>> getIngredientsOf(Id<Recipe> recipeId) {
        return recipeDao.getIngredientsRequiredAmountOf(recipeId.id())
                .zipWith(recipeDao.getIngredientsPresentAmountsOf(recipeId.id()), (required, present) -> {
                    List<RecipeIngredientForDetails> result = new ArrayList<>();
                    var regrouper = new ListRegrouper<>(
                            new ListRegrouper.Group<>(required.iterator(), RecipeFoodForDetailsBaseData::id),
                            new ListRegrouper.Group<>(present.iterator(), RecipeFoodForDetailsBaseData::id),
                            (requiredItem, presentItems) -> result.add(RecipeIngredientForDetails.create(
                                    requiredItem.id(),
                                    requiredItem.foodName(),
                                    UnitAmount.of(requiredItem.scale().multiply(BigDecimal.valueOf(requiredItem.amount())),
                                            requiredItem.abbreviation()),
                                    presentItems.stream()
                                            .map(v -> UnitAmount.of(v.scale().multiply(BigDecimal.valueOf(v.amount())), v.abbreviation()))
                                            .collect(toList())
                            )));
                    regrouper.execute();
                    return result;
                });
    }

    @Override
    public Observable<List<RecipeProductForDetails>> getProductsOf(Id<Recipe> recipeId) {
        return recipeDao.getProductsProducedAmountOf(recipeId.id())
                .zipWith(recipeDao.getProductsPresentAmountsOf(recipeId.id()), (required, present) -> {
                    List<RecipeProductForDetails> result = new ArrayList<>();
                    var regrouper = new ListRegrouper<>(
                            new ListRegrouper.Group<>(required.iterator(), RecipeFoodForDetailsBaseData::id),
                            new ListRegrouper.Group<>(present.iterator(), RecipeFoodForDetailsBaseData::id),
                            (requiredItem, presentItems) -> result.add(RecipeProductForDetails.create(
                                    requiredItem.id(),
                                    requiredItem.foodName(),
                                    UnitAmount.of(requiredItem.scale().multiply(BigDecimal.valueOf(requiredItem.amount())),
                                            requiredItem.abbreviation()),
                                    presentItems.stream()
                                            .map(v -> UnitAmount.of(v.scale().multiply(BigDecimal.valueOf(v.amount())), v.abbreviation()))
                                            .collect(toList())
                            )));
                    regrouper.execute();
                    return result;
                });
    }

    @AutoValue
    abstract static class RecipeFoodForDetailsBaseData {

        public abstract int id();

        public abstract String foodName();

        public abstract String abbreviation();

        public abstract BigDecimal scale();

        public abstract int amount();

        public static RecipeFoodForDetailsBaseData create(int id, String foodName, String abbreviation, BigDecimal scale, int amount) {
            return new AutoValue_RecipeDetailRepositoryImpl_RecipeFoodForDetailsBaseData(id, foodName, abbreviation, scale, amount);
        }
    }
}
