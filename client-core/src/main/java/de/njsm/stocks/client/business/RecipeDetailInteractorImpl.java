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
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

class RecipeDetailInteractorImpl implements RecipeDetailInteractor {

    private final RecipeDetailRepository repository;

    @Inject
    RecipeDetailInteractorImpl(RecipeDetailRepository repository) {
        this.repository = repository;
    }

    @Override
    public Observable<RecipeForDetails> get(Id<Recipe> recipeId) {
        return Observable.zip(
                repository.get(recipeId),
                getIngredientsOf(recipeId),
                getProductsOf(recipeId), (r, i, p) ->
                        RecipeForDetails.create(r.id(), r.name(), r.duration(), r.instructions(), i, p));
    }

    private Observable<List<RecipeIngredientForDetails>> getIngredientsOf(Id<Recipe> recipeId) {
        return repository.getIngredientsRequiredAmountOf(recipeId)
                .zipWith(repository.getIngredientsPresentAmountsOf(recipeId), (required, present) -> {
                    List<RecipeIngredientForDetails> result = new ArrayList<>();
                    var regrouper = new ListRegrouper<>(
                            new ListRegrouper.Group<>(required.iterator(), RecipeFoodForDetailsBaseData::id),
                            new ListRegrouper.Group<>(present.iterator(), PresentRecipeFoodForDetailsBaseData::id),
                            (requiredItem, presentItems) -> result.add(RecipeIngredientForDetails.create(
                                    requiredItem.id(),
                                    requiredItem.foodName(),
                                    UnitAmount.of(requiredItem.scale().multiply(BigDecimal.valueOf(requiredItem.amount())),
                                            requiredItem.abbreviation()),
                                    collectPresentAmounts(requiredItem, presentItems)
                            )));
                    regrouper.execute();
                    result.sort(comparing(RecipeIngredientForDetails::foodName));
                    return result;
                });
    }

    private Observable<List<RecipeProductForDetails>> getProductsOf(Id<Recipe> recipeId) {
        return repository.getProductsProducedAmountOf(recipeId)
                .zipWith(repository.getProductsPresentAmountsOf(recipeId), (required, present) -> {
                    List<RecipeProductForDetails> result = new ArrayList<>();
                    var regrouper = new ListRegrouper<>(
                            new ListRegrouper.Group<>(required.iterator(), RecipeFoodForDetailsBaseData::id),
                            new ListRegrouper.Group<>(present.iterator(), PresentRecipeFoodForDetailsBaseData::id),
                            (requiredItem, presentItems) -> result.add(RecipeProductForDetails.create(
                                    requiredItem.id(),
                                    requiredItem.foodName(),
                                    UnitAmount.of(requiredItem.scale().multiply(BigDecimal.valueOf(requiredItem.amount())),
                                            requiredItem.abbreviation()),
                                    collectPresentAmounts(requiredItem, presentItems)
                            )));
                    regrouper.execute();
                    result.sort(comparing(RecipeProductForDetails::foodName));
                    return result;
                });
    }

    private List<UnitAmount> collectPresentAmounts(RecipeFoodForDetailsBaseData requiredItem, List<PresentRecipeFoodForDetailsBaseData> presentItems) {
        if (presentItems.isEmpty())
            return List.of(UnitAmount.of(BigDecimal.ZERO, requiredItem.foodDefaultUnitAbbreviation()));
        else
            return presentItems.stream()
                    .map(v -> UnitAmount.of(v.scale().multiply(BigDecimal.valueOf(v.amount())), v.abbreviation()))
                    .collect(toList());
    }
}
