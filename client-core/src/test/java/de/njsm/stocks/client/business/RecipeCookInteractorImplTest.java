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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static de.njsm.stocks.client.business.entities.IdImpl.create;
import static java.util.Collections.emptyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipeCookInteractorImplTest {

    private RecipeCookInteractor uut;

    @Mock
    private RecipeCookRepository repository;

    private final IdImpl<Recipe> recipeId = create(1);

    private final RecipeForCooking recipe = RecipeForCooking.create(recipeId, "Pizza");

    @BeforeEach
    void setUp() {
        uut = new RecipeCookInteractorImpl(new RecipeIngredientAmountDistributor(), repository);
        when(repository.getRecipe(recipeId)).thenReturn(Observable.just(recipe));
        when(repository.getProducts(recipeId)).thenReturn(Observable.just(emptyList()));
    }

    @Test
    void missingPresentIngredientWorks() {
        var requiredIngredient = RecipeIngredientForCooking.create(create(2), "Flour", false, create(3), "g", BigDecimal.TEN);
        when(repository.getPresentIngredients(recipeId)).thenReturn(Observable.just(List.of()));
        when(repository.getRequiredIngredients(recipeId)).thenReturn(Observable.just(List.of(requiredIngredient)));

        var actual = uut.getData(recipeId);

        actual.test().assertValue(RecipeCookingFormData.create(
                recipe.name(),
                List.of(RecipeCookingFormDataIngredient.create(
                        requiredIngredient.food(),
                        requiredIngredient.foodName(),
                        requiredIngredient.toBuy(),
                        List.of(RecipeCookingFormDataIngredient.Amount.create(requiredIngredient.amount(), requiredIngredient.abbreviation())),
                        emptyList()
                )),
                emptyList()
        ));
    }

    @Test
    void singleIngredientIsTransformed() {
        var presentIngredient = FoodItemForCooking.create(create(2), create(3), "g", create(4), BigDecimal.TEN, 5);
        var requiredIngredient = RecipeIngredientForCooking.create(presentIngredient.ofType(), "Flour", false, presentIngredient.unit(), presentIngredient.abbreviation(), BigDecimal.TEN);
        when(repository.getPresentIngredients(recipeId)).thenReturn(Observable.just(List.of(presentIngredient)));
        when(repository.getRequiredIngredients(recipeId)).thenReturn(Observable.just(List.of(requiredIngredient)));

        var actual = uut.getData(recipeId);

        actual.test().assertValue(RecipeCookingFormData.create(
                recipe.name(),
                List.of(RecipeCookingFormDataIngredient.create(
                        presentIngredient.ofType(),
                        requiredIngredient.foodName(),
                        requiredIngredient.toBuy(),
                        List.of(RecipeCookingFormDataIngredient.Amount.create(requiredIngredient.amount(), requiredIngredient.abbreviation())),
                        List.of(RecipeCookingFormDataIngredient.PresentAmount.create(
                                RecipeCookingFormDataIngredient.Amount.create(presentIngredient.scale(), presentIngredient.abbreviation()),
                                5, 1))
                )),
                emptyList()
        ));
    }

    @Test
    void twoIngredientsAreReportedSeparately() {
        var requiredIngredient = RecipeIngredientForCooking.create(create(2), "Flour", false, create(3), "g", BigDecimal.TEN);
        var secondRequiredIngredient = RecipeIngredientForCooking.create(create(4), "Tomato", false, create(5), "g", BigDecimal.TEN);
        when(repository.getPresentIngredients(recipeId)).thenReturn(Observable.just(List.of()));
        when(repository.getRequiredIngredients(recipeId)).thenReturn(Observable.just(List.of(requiredIngredient, secondRequiredIngredient)));

        var actual = uut.getData(recipeId);

        actual.test().assertValue(RecipeCookingFormData.create(
                recipe.name(),
                List.of(RecipeCookingFormDataIngredient.create(
                        secondRequiredIngredient.food(),
                        secondRequiredIngredient.foodName(),
                        secondRequiredIngredient.toBuy(),
                        List.of(RecipeCookingFormDataIngredient.Amount.create(secondRequiredIngredient.amount(), secondRequiredIngredient.abbreviation())),
                        emptyList()
                ),
                        RecipeCookingFormDataIngredient.create(
                                requiredIngredient.food(),
                                requiredIngredient.foodName(),
                                requiredIngredient.toBuy(),
                                List.of(RecipeCookingFormDataIngredient.Amount.create(requiredIngredient.amount(), requiredIngredient.abbreviation())),
                                emptyList()
                        )),
                emptyList()
        ));
    }

    @Test
    void twoIngredientsOfSameFoodAreReportedSeparately() {
        var requiredIngredient = RecipeIngredientForCooking.create(create(2), "Flour", false, create(3), "g", BigDecimal.TEN);
        var secondRequiredIngredient = RecipeIngredientForCooking.create(requiredIngredient.food(), requiredIngredient.foodName(), false, create(5), "l", BigDecimal.TEN);
        when(repository.getPresentIngredients(recipeId)).thenReturn(Observable.just(List.of()));
        when(repository.getRequiredIngredients(recipeId)).thenReturn(Observable.just(List.of(requiredIngredient, secondRequiredIngredient)));

        var actual = uut.getData(recipeId);

        actual.test().assertValue(RecipeCookingFormData.create(
                recipe.name(),
                List.of(RecipeCookingFormDataIngredient.create(
                        requiredIngredient.food(),
                        requiredIngredient.foodName(),
                        requiredIngredient.toBuy(),
                        List.of(
                                RecipeCookingFormDataIngredient.Amount.create(requiredIngredient.amount(), requiredIngredient.abbreviation()),
                                RecipeCookingFormDataIngredient.Amount.create(secondRequiredIngredient.amount(), secondRequiredIngredient.abbreviation())),
                        emptyList()
                )),
                emptyList()
        ));
    }
}