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
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.*;

class RecipeCookInteractorImpl implements RecipeCookInteractor {

    private final RecipeIngredientAmountDistributor distributor;

    private final RecipeCookRepository repository;

    private final Scheduler scheduler;

    private final EntityDeleteService<FoodItem> foodItemDeleteService;

    private final FoodItemAddService foodItemAddService;

    private final ErrorRecorder errorRecorder;

    private final Synchroniser synchroniser;

    private final Localiser localiser;

    private final FoodItemFieldPredictor predictor;

    @Inject
    RecipeCookInteractorImpl(RecipeIngredientAmountDistributor distributor, RecipeCookRepository repository, Scheduler scheduler, EntityDeleteService<FoodItem> foodItemDeleteService, FoodItemAddService foodItemAddService, ErrorRecorder errorRecorder, Synchroniser synchroniser, Localiser localiser, FoodItemFieldPredictor predictor) {
        this.distributor = distributor;
        this.repository = repository;
        this.scheduler = scheduler;
        this.foodItemDeleteService = foodItemDeleteService;
        this.foodItemAddService = foodItemAddService;
        this.errorRecorder = errorRecorder;
        this.synchroniser = synchroniser;
        this.localiser = localiser;
        this.predictor = predictor;
    }

    @Override
    public Observable<RecipeCookingFormData> getData(IdImpl<Recipe> recipeId) {
        Observable<RecipeForCooking> recipe = repository.getRecipe(recipeId);
        Observable<List<RecipeIngredientForCooking>> requiredIngredients = repository.getRequiredIngredients(recipeId);
        Observable<List<FoodItemForCooking>> presentIngredients = repository.getPresentIngredients(recipeId);
        var products = repository.getProducts(recipeId);
        return Observable.combineLatest(recipe, requiredIngredients, presentIngredients, products, this::combine);
    }

    private RecipeCookingFormData combine(RecipeForCooking recipe,
                                          List<RecipeIngredientForCooking> requiredIngredients,
                                          List<FoodItemForCooking> presentIngredients,
                                          List<RecipeCookingFormDataProduct> products) {
        var presentIngredientsByFood = presentIngredients.stream()
                .collect(groupingBy(FoodItemForCooking::ofType));
        var requiredIngredientsByFood = requiredIngredients.stream()
                .collect(groupingBy(RecipeIngredientForCooking::food));

        var formDataIngredients = requiredIngredientsByFood.values()
                .stream()
                .map(v -> toFormDataIngredient(presentIngredientsByFood.getOrDefault(v.get(0).food(), emptyList()), v))
                .collect(toList());

        return RecipeCookingFormData.create(recipe.name(), formDataIngredients, products);
    }

    private RecipeCookingFormDataIngredient toFormDataIngredient(List<FoodItemForCooking> presentFoodItemsOfThisIngredient, List<RecipeIngredientForCooking> ingredientAmounts) {
        var ingredient = ingredientAmounts.get(0);
        var distributedAmounts = distributor.distribute(
                aggregateAmountsByUnit(ingredientAmounts),
                presentFoodItemsOfThisIngredient
                        .stream()
                        .map(FoodItemForCooking::toPresentAmount)
                        .collect(Collectors.toList()));

        return RecipeCookingFormDataIngredient.create(ingredient.food(), ingredient.foodName(), ingredient.toBuy(),
                ingredientAmounts
                        .stream()
                        .map(RecipeIngredientForCooking::toFormDataRequiredAmount)
                        .collect(toList()),
                presentFoodItemsOfThisIngredient
                        .stream()
                        .map(v -> v.toFormDataPresentAmount(distributedAmounts.get(v.scaledUnit())))
                        .collect(toList()));
    }

    private List<RecipeIngredientAmountDistributor.RequiredAmount> aggregateAmountsByUnit(List<RecipeIngredientForCooking> ingredientAmounts) {
        return ingredientAmounts.stream()
                .collect(groupingBy(RecipeIngredientForCooking::unit,
                        mapping(RecipeIngredientForCooking::toRequiredAmount,
                                reducing((a, b) -> RecipeIngredientAmountDistributor.RequiredAmount.create(a.unit(), a.scale().add(b.scale())))
                        )))
                .values()
                .stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

    @Override
    public void cook(RecipeCookingForm recipeCookingForm) {
        scheduler.schedule(Job.create(Job.Type.COOK_RECIPE, () -> doInBackground(recipeCookingForm)));
    }

    private void doInBackground(RecipeCookingForm recipeCookingForm) {
        var foodItemsToDelete = repository.getFoodItemsForCooking(recipeCookingForm.ingredients());
        checkout(foodItemsToDelete);
        recipeCookingForm.products()
                .forEach(this::add);
        synchroniser.synchronise();
    }

    private FoodItemForm toFoodItem(RecipeCookingProductToProduce product) {
        var prediction = predictor.predictFor(product.food());
        return FoodItemForm.create(
                prediction.predictEatBy(),
                product.food().id(),
                prediction.predictLocation().id(),
                product.scaledUnit().id());
    }

    private void add(RecipeCookingProductToProduce product) {
        FoodItemForm item = toFoodItem(product);
        FoodItemToAdd networkItem = item.toNetwork(localiser);
        for (int i = 0; i < product.count(); i++)
            try {
                foodItemAddService.add(networkItem);
            } catch (SubsystemException e) {
                errorRecorder.recordFoodItemAddError(e, item.toErrorRecording(localiser));
            }
    }

    private void checkout(List<FoodItemForDeletion> foodItemsToDelete) {
        for (var item : foodItemsToDelete) {
            try {
                foodItemDeleteService.delete(item);
            } catch (SubsystemException e) {
                errorRecorder.recordFoodItemDeleteError(e, item);
            }
        }
    }
}
