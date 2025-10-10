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

package de.njsm.stocks.client.fragment.recipeedit;

import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.android.material.textfield.TextInputEditText;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.RecipeEditInteractor;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.navigation.RecipeEditNavigator;
import de.njsm.stocks.client.presenter.UnitAmountRenderStrategy;
import de.njsm.stocks.client.testdata.ScaledUnitsForSelection;
import de.njsm.stocks.client.ui.R;
import io.reactivex.rxjava3.core.Maybe;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.client.Matchers.nestedScrollTo;
import static de.njsm.stocks.client.Matchers.recyclerView;
import static de.njsm.stocks.client.fragment.Util.menuItem;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class RecipeEditFragmentTest {

    private FragmentScenario<RecipeEditFragment> scenario;

    private RecipeEditInteractor recipeEditInteractor;

    private RecipeEditNavigator navigator;

    private RecipeEditFormData selectableData;

    private UnitAmountRenderStrategy unitRenderStrategy;

    @Before
    public void setup() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        reset(navigator);
        reset(recipeEditInteractor);
        selectableData = getInputData();
        unitRenderStrategy = new UnitAmountRenderStrategy();
        when(recipeEditInteractor.getForm(any())).thenReturn(Maybe.just(selectableData));
        when(navigator.getRecipe(any())).thenReturn(getInputData().recipe());
        scenario = FragmentScenario.launchInContainer(RecipeEditFragment.class, new Bundle(), R.style.StocksTheme);
    }

    @After
    public void tearDown() {
        reset(navigator);
        reset(recipeEditInteractor);
    }

    @Test
    public void uiIsShown() {
        var recipe = getInputData();
        onView(allOf(isDescendantOfA(withId(R.id.fragment_recipe_form_name)), withClassName(is(TextInputEditText.class.getName()))))
                .check(matches(withText(recipe.recipe().name())));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_recipe_form_duration)), withClassName(is(TextInputEditText.class.getName()))))
                .check(matches(withText(String.valueOf(recipe.recipe().duration().toMinutes()))));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_recipe_form_instructions)), withClassName(is(TextInputEditText.class.getName()))))
                .check(matches(withText(recipe.recipe().instructions())));

        int i = 0;
        for (var item : recipe.ingredients()) {
            onView(recyclerView(R.id.fragment_recipe_form_ingredient_list)
                    .atPositionOnView(i, R.id.item_recipe_food_amount))
                    .check(matches(hasDescendant(withText(String.valueOf(item.amount())))));
            onView(recyclerView(R.id.fragment_recipe_form_ingredient_list)
                    .atPositionOnView(i, R.id.item_recipe_food_food))
                    .check(matches(hasDescendant(withText(recipe.availableFood().get(item.ingredientListItemPosition()).name()))));
            onView(recyclerView(R.id.fragment_recipe_form_ingredient_list)
                    .atPositionOnView(i, R.id.item_recipe_food_unit))
                    .check(matches(hasDescendant(withText(unitRenderStrategy.render(recipe.availableUnits().get(item.unitListItemPosition()))))));
            i++;
        }

        i = 0;
        for (var item : recipe.products()) {
            onView(recyclerView(R.id.fragment_recipe_form_product_list)
                    .atPositionOnView(i, R.id.item_recipe_food_amount))
                    .check(matches(hasDescendant(withText(String.valueOf(item.amount())))));
            onView(recyclerView(R.id.fragment_recipe_form_product_list)
                    .atPositionOnView(i, R.id.item_recipe_food_food))
                    .check(matches(hasDescendant(withText(recipe.availableFood().get(item.productListItemPosition()).name()))));
            onView(recyclerView(R.id.fragment_recipe_form_product_list)
                    .atPositionOnView(i, R.id.item_recipe_food_unit))
                    .check(matches(hasDescendant(withText(unitRenderStrategy.render(recipe.availableUnits().get(item.unitListItemPosition()))))));
            i++;
        }
    }

    @Test
    public void submittingBareRecipeWorks() {
        var input = getInputData();
        String editedName = input.recipe().name() + " edited";
        String editedInstructions = input.recipe().instructions() + " edited";
        Duration editedDuration = input.recipe().duration().plusMinutes(1);
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_recipe_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(editedName));
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_recipe_form_duration)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(String.valueOf(editedDuration.toMinutes())));
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_recipe_form_instructions)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(editedInstructions));

        scenario.onFragment(v -> v.onMenuItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        ArgumentCaptor<RecipeEditForm> captor = ArgumentCaptor.forClass(RecipeEditForm.class);
        verify(recipeEditInteractor).edit(captor.capture());
        verify(navigator).back();
        var actual = captor.getValue();
        assertEquals(editedName, actual.recipe().name());
        assertEquals(editedInstructions, actual.recipe().instructions());
        assertEquals(editedDuration, actual.recipe().duration());
    }

    @Test
    public void submittingWithoutNameIsProhibited() {
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_recipe_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(""));

        scenario.onFragment(v -> v.onMenuItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        verify(recipeEditInteractor, never()).edit(any());
        onView(withId(R.id.fragment_recipe_form_name))
                .check(matches(hasDescendant(withText(R.string.error_may_not_be_empty))));
    }

    @Test
    public void addingWithIngredientWorks() {
        var input = getInputData();
        var expectedIngredients = new ArrayList<>(input.ingredients());
        expectedIngredients.add(RecipeIngredientEditFormData.create(-1, 3, 1, input.availableUnits().get(1), 1, input.availableFood().get(1)));
        addingSingleFoodWorks(
                R.id.fragment_recipe_form_add_ingredient,
                R.id.fragment_recipe_form_ingredient_list,
                expectedIngredients,
                input.products(),
                input.ingredients().size());
    }

    @Test
    public void addingWithProductWorks() {
        var input = getInputData();
        var expectedProducts = new ArrayList<>(input.products());
        expectedProducts.add(RecipeProductEditFormData.create(-1, 3, 1, input.availableUnits().get(1), 1, input.availableFood().get(1)));
        addingSingleFoodWorks(
                R.id.fragment_recipe_form_add_product,
                R.id.fragment_recipe_form_product_list,
                input.ingredients(),
                expectedProducts,
                input.products().size());
    }

    private void addingSingleFoodWorks(@IdRes int addButton,
                                       @IdRes int list,
                                       List<RecipeIngredientEditFormData> ingredients,
                                       List<RecipeProductEditFormData> products,
                                       int position) {
        var input = getInputData();
        addFood(addButton, list, position);

        scenario.onFragment(v -> v.onMenuItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        var expected = RecipeEditForm.create(input.recipe(),
                ingredients,
                products);
        verify(recipeEditInteractor).edit(expected);
        verify(navigator).back();
    }

    private static void addFood(int addButton, int list, int position) {
        onView(withId(addButton)).perform(nestedScrollTo(), click(), nestedScrollTo());
        onView(allOf(isDescendantOfA(recyclerView(list)
                        .atPositionOnView(position, R.id.item_recipe_food_amount)),
                withClassName(is(TextInputEditText.class.getName()))))
                .perform(replaceText("3"));
        onView(recyclerView(list)
                .atPositionOnView(position, R.id.item_recipe_food_food_text)).perform(
                scrollTo(),
                typeText("Fl"));
        onView(withText("Flour")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        onView(recyclerView(list)
                .atPositionOnView(position, R.id.item_recipe_food_unit)).perform(click());
        onData(anything()).atPosition(1).perform(scrollTo(), click());
    }

    @Test
    public void addingAndRemovingIngredientWorks() {
        var input = getInputData();
        addingAndRemovingSingleFoodWorks(R.id.fragment_recipe_form_add_ingredient,
                R.id.fragment_recipe_form_ingredient_list, input.ingredients().size());
    }

    @Test
    public void addingAndRemovingProductWorks() {
        var input = getInputData();
        addingAndRemovingSingleFoodWorks(R.id.fragment_recipe_form_add_product,
                R.id.fragment_recipe_form_product_list, input.products().size());
    }

    private void addingAndRemovingSingleFoodWorks(@IdRes int addButton,
                                                  @IdRes int list, int position) {
        var input = getInputData();

        addFood(addButton, list, position);
        onView(recyclerView(list).atPosition(position)).perform(swipeRight());

        scenario.onFragment(v -> v.onMenuItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        var expected = RecipeEditForm.create(input.recipe(),
                input.ingredients(),
                input.products());
        verify(recipeEditInteractor).edit(expected);
        verify(navigator).back();
    }

    @Test
    public void formDataIsPersisted() {
        var recipe = getInputData();
        var expectedIngredients = new ArrayList<>(recipe.ingredients());
        expectedIngredients.add(RecipeIngredientEditFormData.create(-1, 3, 1, recipe.availableUnits().get(1), 1, recipe.availableFood().get(1)));
        var expectedProducts = new ArrayList<>(recipe.products());
        expectedProducts.add(RecipeProductEditFormData.create(-1, 3, 1, recipe.availableUnits().get(1), 1, recipe.availableFood().get(1)));

        addFood(R.id.fragment_recipe_form_add_ingredient, R.id.fragment_recipe_form_ingredient_list, recipe.ingredients().size());
        addFood(R.id.fragment_recipe_form_add_product, R.id.fragment_recipe_form_product_list, recipe.products().size());

        scenario.recreate();
        scenario.onFragment(v -> v.onMenuItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        var expected = RecipeEditForm.create(recipe.recipe(),
                expectedIngredients,
                expectedProducts);
        verify(recipeEditInteractor).edit(expected);
        verify(navigator).back();
    }

    @Test
    public void formDataIsPersistedWhenRecreatingTwice() {
        var recipe = getInputData();
        var expectedIngredients = new ArrayList<>(recipe.ingredients());
        expectedIngredients.add(RecipeIngredientEditFormData.create(-1, 3, 1, recipe.availableUnits().get(1), 1, recipe.availableFood().get(1)));
        var expectedProducts = new ArrayList<>(recipe.products());
        expectedProducts.add(RecipeProductEditFormData.create(-1, 3, 1, recipe.availableUnits().get(1), 1, recipe.availableFood().get(1)));

        addFood(R.id.fragment_recipe_form_add_ingredient, R.id.fragment_recipe_form_ingredient_list, recipe.ingredients().size());
        addFood(R.id.fragment_recipe_form_add_product, R.id.fragment_recipe_form_product_list, recipe.products().size());

        scenario.recreate();
        scenario.recreate();
        scenario.onFragment(v -> v.onMenuItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        var expected = RecipeEditForm.create(recipe.recipe(),
                expectedIngredients,
                expectedProducts);
        verify(recipeEditInteractor).edit(expected);
        verify(navigator).back();
    }

    @Inject
    public void setRecipeEditInteractor(RecipeEditInteractor recipeEditInteractor) {
        this.recipeEditInteractor = recipeEditInteractor;
    }

    @Inject
    public void setNavigator(RecipeEditNavigator navigator) {
        this.navigator = navigator;
    }

    @NotNull
    private static RecipeEditFormData getInputData() {
        return RecipeEditFormData.create(
                RecipeEditBaseData.create(
                        4,
                        "Pizza",
                        "just bake",
                        Duration.ofHours(2)
                ),
                List.of(
                        RecipeIngredientEditFormData.create(
                                4,
                                1,
                                1,
                                IdImpl.create(2),
                                1,
                                IdImpl.create(7)
                        ),
                        RecipeIngredientEditFormData.create(
                                5,
                                2,
                                0,
                                IdImpl.create(1),
                                0,
                                IdImpl.create(3)
                        )
                ),
                List.of(
                        RecipeProductEditFormData.create(
                                3,
                                1,
                                2,
                                IdImpl.create(4),
                                2,
                                IdImpl.create(12)
                        )
                ),
                List.of(
                        FoodForSelection.create(3, "Banana"),
                        FoodForSelection.create(7, "Flour"),
                        FoodForSelection.create(12, "Water")
                ),
                ScaledUnitsForSelection.generate());
    }
}
