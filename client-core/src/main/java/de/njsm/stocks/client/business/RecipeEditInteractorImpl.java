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
import de.njsm.stocks.client.execution.Scheduler;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

class RecipeEditInteractorImpl implements RecipeEditInteractor {

    private final RecipeEditRepository repository;

    private final RecipeEditService service;

    private final ErrorRecorder errorRecorder;

    private final Scheduler scheduler;

    private final Synchroniser synchroniser;

    @Inject
    RecipeEditInteractorImpl(RecipeEditRepository repository, RecipeEditService service, ErrorRecorder errorRecorder, Scheduler scheduler, Synchroniser synchroniser) {
        this.repository = repository;
        this.service = service;
        this.errorRecorder = errorRecorder;
        this.scheduler = scheduler;
        this.synchroniser = synchroniser;
    }

    @Override
    public Maybe<RecipeEditFormData> getForm(Id<Recipe> recipeId) {
        Maybe<RecipeEditBaseData> recipe = repository.getRecipe(recipeId);
        Maybe<List<RecipeIngredientEditData>> ingredients = repository.getIngredients(recipeId);
        Maybe<List<RecipeProductEditData>> products = repository.getProducts(recipeId);
        Observable<List<FoodForSelection>> availableFood = repository.getFood();
        Observable<List<ScaledUnitForSelection>> availableUnits = repository.getUnits();

        return Maybe.zip(recipe, ingredients, products,
                availableFood.firstElement(), availableUnits.firstElement(), this::transform)
                .subscribeOn(scheduler.into());
    }

    private RecipeEditFormData transform(RecipeEditBaseData r, List<RecipeIngredientEditData> i, List<RecipeProductEditData> p, List<FoodForSelection> f, List<ScaledUnitForSelection> u) {
        Map<Id<ScaledUnit>, Integer> indexedUnits = idToListPosition(u);
        Map<Id<Food>, Integer> indexedFood = idToListPosition(f);

        var formIngredients = i.stream()
                .map(v -> RecipeIngredientEditFormData.create(v.id(), v.amount(), indexedUnits.get(v.unit()), v.unit(), indexedFood.get(v.ingredient()), v.ingredient()))
                .collect(Collectors.toList());

        var formProducts = p.stream()
                .map(v -> RecipeProductEditFormData.create(v.id(), v.amount(), indexedUnits.get(v.unit()), v.unit(), indexedFood.get(v.product()), v.product()))
                .collect(Collectors.toList());

        return RecipeEditFormData.create(
                r, formIngredients, formProducts, f, u
        );
    }

    private <E extends Entity<E>, T extends Id<E>> Map<Id<E>, Integer> idToListPosition(List<T> list) {
        TreeMap<Id<E>, Integer> result = new TreeMap<>(Comparator.comparing(Id::id));
        int i = 0;
        for (T item : list) {
            result.put(item, i);
            i++;
        }
        return result;
    }

    @Override
    public void edit(RecipeEditForm form) {
        scheduler.schedule(Job.create(Job.Type.EDIT_RECIPE, () -> runInBackground(form)));
    }

    private void runInBackground(RecipeEditForm form) {
        RecipeIngredientEditMatcher ingredientMatcher = new RecipeIngredientEditMatcher(repository.getIngredients(form.recipe()).blockingGet(), form.ingredients(), repository::getRecipeIngredientVersion);
        RecipeProductEditMatcher productMatcher = new RecipeProductEditMatcher(repository.getProducts(form.recipe()).blockingGet(), form.products(), repository::getRecipeProductVersion);
        try {
            service.edit(RecipeEditNetworkData.create(RecipeEditBaseNetworkData.create(
                            form.recipe().id(),
                            repository.getRecipeVersion(form.recipe()).version(),
                            form.recipe().name(),
                            form.recipe().instructions(),
                            form.recipe().duration()),
                    ingredientMatcher.getToAdd(),
                    ingredientMatcher.getToDelete(),
                    ingredientMatcher.getToEdit(),
                    productMatcher.getToAdd(),
                    productMatcher.getToDelete(),
                    productMatcher.getToEdit()
            ));
            synchroniser.synchronise();
        } catch (SubsystemException e) {
            errorRecorder.recordRecipeEditError(e, form);
            synchroniser.synchroniseAfterError(e);
        }
    }
}
