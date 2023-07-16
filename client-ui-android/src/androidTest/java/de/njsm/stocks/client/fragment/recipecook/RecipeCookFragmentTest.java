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

package de.njsm.stocks.client.fragment.recipecook;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.FoodToBuyInteractor;
import de.njsm.stocks.client.business.RecipeCookInteractor;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.navigation.RecipeCookNavigator;
import de.njsm.stocks.client.presenter.UnitAmountRenderStrategy;
import de.njsm.stocks.client.ui.R;
import io.reactivex.rxjava3.core.Observable;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static de.njsm.stocks.client.Matchers.*;
import static de.njsm.stocks.client.business.entities.IdImpl.create;
import static de.njsm.stocks.client.fragment.Util.menuItem;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class RecipeCookFragmentTest {

    private FragmentScenario<RecipeCookFragment> scenario;

    private RecipeCookInteractor recipeCookInteractor;

    private FoodToBuyInteractor foodToBuyInteractor;

    private RecipeCookNavigator navigator;

    private UnitAmountRenderStrategy unitRenderStrategy;

    @Before
    public void setup() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        reset(navigator);
        reset(recipeCookInteractor);
        IdImpl<Recipe> recipeId = create(42);
        unitRenderStrategy = new UnitAmountRenderStrategy();
        when(recipeCookInteractor.getData(recipeId)).thenReturn(Observable.just(getInputData()));
        when(navigator.getRecipe(any())).thenReturn(recipeId);
        scenario = FragmentScenario.launchInContainer(RecipeCookFragment.class, new Bundle(), R.style.StocksTheme);
    }

    @After
    public void tearDown() {
        reset(navigator);
        reset(recipeCookInteractor);
    }

    @Test
    public void uiIsShown() {
        var recipe = getInputData();

        int i = 0;
        for (var ingredient : recipe.ingredients()) {
            onView(recyclerView(R.id.fragment_recipe_cook_ingredients).atPositionOnView(i, R.id.item_recipe_item_food_name))
                    .check(matches(withText(ingredient.name())));
            onView(recyclerView(R.id.fragment_recipe_cook_ingredients).atPositionOnView(i, R.id.item_recipe_item_scaled_unit))
                    .check(matches(withText(unitRenderStrategy.render(ingredient.requiredAmount()))));
            onView(recyclerView(R.id.fragment_recipe_cook_ingredients)
                    .atPositionOnView(i, R.id.item_recipe_item_shopping_cart))
                    .check(matches(withImageDrawable(ingredient.toBuy() ? R.drawable.baseline_remove_shopping_cart_black_24 :
                            R.drawable.baseline_add_shopping_cart_black_24)));

            int j = 0;
            for (var amount : ingredient.presentAmount()) {
                onView(allOf(isDescendantOfA(recyclerView(R.id.fragment_recipe_cook_ingredients).atPosition(i)),
                        recyclerView(R.id.item_recipe_item_amounts).atPositionOnView(j, R.id.item_amount_incrementor_stock_counter)))
                        .check(matches(withText(unitRenderStrategy.render(amount.scaleSelected()))));
                onView(allOf(isDescendantOfA(recyclerView(R.id.fragment_recipe_cook_ingredients).atPosition(i)),
                        recyclerView(R.id.item_recipe_item_amounts).atPositionOnView(j, R.id.item_amount_incrementor_stock_counter)))
                        .check(matches(withText(unitRenderStrategy.render(amount.scaleSelected()))));
                onView(allOf(isDescendantOfA(recyclerView(R.id.fragment_recipe_cook_ingredients).atPosition(i)),
                        recyclerView(R.id.item_recipe_item_amounts).atPositionOnView(j, R.id.item_amount_incrementor_max_counter)))
                        .check(matches(withText(unitRenderStrategy.render(amount.scalePresent()))));
                onView(allOf(isDescendantOfA(recyclerView(R.id.fragment_recipe_cook_ingredients).atPosition(i)),
                        recyclerView(R.id.item_recipe_item_amounts).atPositionOnView(j, R.id.item_amount_incrementor_unit)))
                        .check(matches(withText(unitRenderStrategy.renderUnitSymbol(amount.amount()))));
                j++;
            }

            i++;
        }

        i = 0;
        for (var product : recipe.products()) {
            onView(recyclerView(R.id.fragment_recipe_cook_products).atPositionOnView(i, R.id.item_recipe_item_food_name))
                    .check(matches(withText(product.name())));
            onView(recyclerView(R.id.fragment_recipe_cook_products).atPositionOnView(i, R.id.item_recipe_item_scaled_unit))
                    .check(matches(withText(unitRenderStrategy.render(product.producedAmount()))));

            onView(allOf(isDescendantOfA(recyclerView(R.id.fragment_recipe_cook_products).atPosition(i)),
                    recyclerView(R.id.item_recipe_item_amounts).atPositionOnView(0, R.id.item_amount_incrementor_stock_counter)))
                    .check(matches(withText(unitRenderStrategy.render(product.producedAmount().scaledProductedAmount()))));
            onView(allOf(isDescendantOfA(recyclerView(R.id.fragment_recipe_cook_products).atPosition(i)),
                    recyclerView(R.id.item_recipe_item_amounts).atPositionOnView(0, R.id.item_amount_incrementor_unit)))
                    .check(matches(withText(unitRenderStrategy.renderUnitSymbol(product.producedAmount()))));

            i++;
        }
    }

    @Test
    public void pressingIngredientAmountAddButtonWorks() {
        var recipe = getInputData();
        var ingredient = recipe.ingredients().get(0);

        onView(allOf(isDescendantOfA(recyclerView(R.id.fragment_recipe_cook_ingredients).atPosition(0)),
                recyclerView(R.id.item_recipe_item_amounts).atPositionOnView(0, R.id.item_amount_incrementor_plus)))
                .perform(click());

        onView(allOf(isDescendantOfA(recyclerView(R.id.fragment_recipe_cook_ingredients).atPosition(0)),
                recyclerView(R.id.item_recipe_item_amounts).atPositionOnView(0, R.id.item_amount_incrementor_stock_counter)))
                .check(matches(withText(unitRenderStrategy.render(ingredient.presentAmount().get(0).increase().scaleSelected()))));
    }

    @Test
    public void pressingIngredientAmountRemoveButtonWorks() {
        var recipe = getInputData();
        var ingredient = recipe.ingredients().get(0);

        onView(allOf(isDescendantOfA(recyclerView(R.id.fragment_recipe_cook_ingredients).atPosition(0)),
                recyclerView(R.id.item_recipe_item_amounts).atPositionOnView(0, R.id.item_amount_incrementor_minus)))
                .perform(click());

        onView(allOf(isDescendantOfA(recyclerView(R.id.fragment_recipe_cook_ingredients).atPosition(0)),
                recyclerView(R.id.item_recipe_item_amounts).atPositionOnView(0, R.id.item_amount_incrementor_stock_counter)))
                .check(matches(withText(unitRenderStrategy.render(ingredient.presentAmount().get(0).decrease().scaleSelected()))));
    }

    @Test
    public void pressingProductAmountAddButtonWorks() {
        var recipe = getInputData();
        var product = recipe.products().get(0);

        onView(allOf(isDescendantOfA(recyclerView(R.id.fragment_recipe_cook_products).atPosition(0)),
                recyclerView(R.id.item_recipe_item_amounts).atPositionOnView(0, R.id.item_amount_incrementor_plus)))
                .perform(click());

        onView(allOf(isDescendantOfA(recyclerView(R.id.fragment_recipe_cook_products).atPosition(0)),
                recyclerView(R.id.item_recipe_item_amounts).atPositionOnView(0, R.id.item_amount_incrementor_stock_counter)))
                .check(matches(withText(unitRenderStrategy.render(product.producedAmount().increase().scaledProductedAmount()))));
    }

    @Test
    public void pressingProductAmountRemoveButtonWorks() {
        var recipe = getInputData();
        var product = recipe.products().get(0);

        onView(allOf(isDescendantOfA(recyclerView(R.id.fragment_recipe_cook_products).atPosition(0)),
                recyclerView(R.id.item_recipe_item_amounts).atPositionOnView(0, R.id.item_amount_incrementor_minus)))
                .perform(click());

        onView(allOf(isDescendantOfA(recyclerView(R.id.fragment_recipe_cook_products).atPosition(0)),
                recyclerView(R.id.item_recipe_item_amounts).atPositionOnView(0, R.id.item_amount_incrementor_stock_counter)))
                .check(matches(withText(unitRenderStrategy.render(product.producedAmount().decrease().scaledProductedAmount()))));
    }

    @Test
    public void pressingToBuyIsPropagated() {
        var recipe = getInputData();
        var ingredient = recipe.ingredients().get(1);

        onView(recyclerView(R.id.fragment_recipe_cook_ingredients).atPositionOnView(1, R.id.item_recipe_item_shopping_cart))
                .perform(click());

        verify(foodToBuyInteractor).manageFoodToBuy(FoodToToggleBuy.create(ingredient.id()));
    }

    @Test
    public void checkingOutNavigatesBack() {
        scenario.onFragment(f -> f.onMenuItemSelected(menuItem(f.getContext(), R.id.menu_check)));

        verify(navigator).back();
    }

    @Inject
    public void setRecipeCookInteractor(RecipeCookInteractor recipeCookInteractor) {
        this.recipeCookInteractor = recipeCookInteractor;
    }

    @Inject
    public void setNavigator(RecipeCookNavigator navigator) {
        this.navigator = navigator;
    }

    @Inject
    public void setFoodToBuyInteractor(FoodToBuyInteractor foodToBuyInteractor) {
        this.foodToBuyInteractor = foodToBuyInteractor;
    }

    @NotNull
    private static RecipeCookingFormData getInputData() {
        return RecipeCookingFormData.create(
                "Pizza",
                List.of(
                        RecipeCookingFormDataIngredient.create(
                                create(1),
                                "Flour",
                                false,
                                List.of(
                                        RecipeCookingFormDataIngredient.Amount.create(
                                                BigDecimal.valueOf(125),
                                                "g"
                                        )
                                ),
                                List.of(
                                        RecipeCookingFormDataIngredient.PresentAmount.create(
                                                RecipeCookingFormDataIngredient.Amount.create(
                                                        BigDecimal.valueOf(100),
                                                        "g"
                                                ),
                                                create(1),
                                                10,
                                                1
                                        )
                                )
                        ),
                        RecipeCookingFormDataIngredient.create(
                                create(2),
                                "Tomato",
                                true,
                                List.of(
                                        RecipeCookingFormDataIngredient.Amount.create(
                                                BigDecimal.valueOf(0.1),
                                                "l"
                                        )
                                ),
                                List.of(
                                        RecipeCookingFormDataIngredient.PresentAmount.create(
                                                RecipeCookingFormDataIngredient.Amount.create(
                                                        BigDecimal.valueOf(0.1),
                                                        "l"
                                                ),
                                                create(2),
                                                5,
                                                1
                                        ),
                                        RecipeCookingFormDataIngredient.PresentAmount.create(
                                                RecipeCookingFormDataIngredient.Amount.create(
                                                        BigDecimal.valueOf(1),
                                                        "piece"
                                                ),
                                                create(3),
                                                5,
                                                0
                                        )
                                )
                        ),
                        RecipeCookingFormDataIngredient.create(
                                create(3),
                                "Mozzarella",
                                true,
                                List.of(
                                        RecipeCookingFormDataIngredient.Amount.create(
                                                BigDecimal.valueOf(150),
                                                "g"
                                        )
                                ),
                                List.of()
                        )
                ),
                List.of(
                        RecipeCookingFormDataProduct.create(
                                create(4),
                                "Pizza",
                                RecipeCookingFormDataProduct.Amount.create(
                                        create(2),
                                        BigDecimal.valueOf(2),
                                        "portion",
                                        2
                                )
                        )
                )
        );
    }
}
