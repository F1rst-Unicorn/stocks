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

package de.njsm.stocks.client.fragment.recipeadd;

import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.android.material.textfield.TextInputEditText;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.RecipeAddInteractor;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.navigation.Navigator;
import de.njsm.stocks.client.testdata.ScaledUnitsForSelection;
import de.njsm.stocks.client.ui.R;
import io.reactivex.rxjava3.core.Observable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.Duration;
import java.util.List;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.client.Matchers.recyclerView;
import static de.njsm.stocks.client.fragment.Util.menuItem;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.anything;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class RecipeAddFragmentTest {

    private FragmentScenario<RecipeAddFragment> scenario;

    private RecipeAddInteractor recipeAddInteractor;

    private Navigator navigator;

    private RecipeAddData selectableData;

    @Before
    public void setup() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        reset(navigator);
        reset(recipeAddInteractor);
        selectableData = RecipeAddData.create(
                List.of(
                        FoodForSelection.create(3, "Banana"),
                        FoodForSelection.create(7, "Flour"),
                        FoodForSelection.create(12, "Water")
                ),
                ScaledUnitsForSelection.generate());
        when(recipeAddInteractor.getData()).thenReturn(Observable.just(selectableData));
        scenario = FragmentScenario.launchInContainer(RecipeAddFragment.class, new Bundle(), R.style.StocksTheme);
    }

    @After
    public void tearDown() {
        reset(navigator);
        reset(recipeAddInteractor);
    }

    @Test
    public void uiIsShown() {
        onView(withId(R.id.fragment_recipe_form_name)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.fragment_recipe_form_duration)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.fragment_recipe_form_instructions)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.fragment_recipe_form_add_ingredient)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.fragment_recipe_form_add_product)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void submittingBareRecipeWorks() {
        var recipe = getInput();

        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_recipe_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(recipe.name()));
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_recipe_form_duration)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(String.valueOf(recipe.duration().toMinutes())));
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_recipe_form_instructions)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(recipe.instructions()));
        scenario.onFragment(v -> v.onMenuItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        verify(recipeAddInteractor).add(recipe);
        verify(navigator).back();
    }

    @Test
    public void submittingWithoutNameIsProhibited() {
        scenario.onFragment(v -> v.onMenuItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        verify(recipeAddInteractor, never()).add(any());
        onView(withId(R.id.fragment_recipe_form_name))
                .check(matches(hasDescendant(withText(R.string.error_may_not_be_empty))));
    }

    @Test
    public void clearingNameShowsError() {
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_recipe_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText("some name"), clearText());

        onView(withId(R.id.fragment_recipe_form_name))
                .check(matches(hasDescendant(withText(R.string.error_may_not_be_empty))));
    }

    @Test
    public void addingWithIngredientWorks() {
        addingSingleFoodWorks(
                R.id.fragment_recipe_form_add_ingredient,
                R.id.fragment_recipe_form_ingredient_list,
                List.of(RecipeIngredientToAdd.create(3, selectableData.availableFood().get(1), selectableData.availableUnits().get(1))),
                emptyList());
    }

    @Test
    public void addingWithProductWorks() {
        addingSingleFoodWorks(
                R.id.fragment_recipe_form_add_product,
                R.id.fragment_recipe_form_product_list,
                emptyList(),
                List.of(RecipeProductToAdd.create(3, selectableData.availableFood().get(1), selectableData.availableUnits().get(1))));
    }

    private void addingSingleFoodWorks(@IdRes int addButton,
                                       @IdRes int list,
                                       List<RecipeIngredientToAdd> ingredients,
                                       List<RecipeProductToAdd> products) {
        var recipe = getInput();

        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_recipe_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(recipe.name()));

        addFood(addButton, list);
        scenario.onFragment(v -> v.onMenuItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        var expected = RecipeAddForm.create(recipe.name(), "", Duration.ZERO,
                ingredients,
                products);
        verify(recipeAddInteractor).add(expected);
        verify(navigator).back();
    }

    private static void addFood(int addButton, int list) {
        onView(withId(addButton)).perform(click());
        onView(allOf(isDescendantOfA(recyclerView(list)
                .atPositionOnView(0, R.id.item_recipe_food_amount)),
                withClassName(is(TextInputEditText.class.getName()))))
                .perform(replaceText("3"));
        onView(recyclerView(list)
                .atPositionOnView(0, R.id.item_recipe_food_food)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        onView(recyclerView(list)
                .atPositionOnView(0, R.id.item_recipe_food_unit)).perform(click());
        onData(anything()).atPosition(1).perform(click());
    }

    @Test
    public void addingAndRemovingIngredientWorks() {
        addingAndRemovingSingleFoodWorks(R.id.fragment_recipe_form_add_ingredient,
                R.id.fragment_recipe_form_ingredient_list);
    }

    @Test
    public void addingAndRemovingProductWorks() {
        addingAndRemovingSingleFoodWorks(R.id.fragment_recipe_form_add_product,
                R.id.fragment_recipe_form_product_list);
    }

    private void addingAndRemovingSingleFoodWorks(@IdRes int addButton,
                                       @IdRes int list) {
        var recipe = getInput();

        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_recipe_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(recipe.name()));

        addFood(addButton, list);
        onView(recyclerView(list).atPosition(0)).perform(swipeRight());
        scenario.onFragment(v -> v.onMenuItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        var expected = RecipeAddForm.create(recipe.name(), "", Duration.ZERO,
                emptyList(),
                emptyList());
        verify(recipeAddInteractor).add(expected);
        verify(navigator).back();
    }

    @Test
    public void formDataIsPersisted() {
        var recipe = getInput();

        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_recipe_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(recipe.name()));
        addFood(R.id.fragment_recipe_form_add_ingredient, R.id.fragment_recipe_form_ingredient_list);
        addFood(R.id.fragment_recipe_form_add_product, R.id.fragment_recipe_form_product_list);

        scenario.recreate();
        scenario.onFragment(v -> v.onMenuItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        var expected = RecipeAddForm.create(recipe.name(), "", Duration.ZERO,
                List.of(RecipeIngredientToAdd.create(3, selectableData.availableFood().get(1), selectableData.availableUnits().get(1))),
                List.of(RecipeProductToAdd.create(3, selectableData.availableFood().get(1), selectableData.availableUnits().get(1))));
        verify(recipeAddInteractor).add(expected);
        verify(navigator).back();
    }

    @Test
    public void formDataIsPersistedWhenRecreatingTwice() {
        var recipe = getInput();

        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_recipe_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(recipe.name()));
        addFood(R.id.fragment_recipe_form_add_ingredient, R.id.fragment_recipe_form_ingredient_list);
        addFood(R.id.fragment_recipe_form_add_product, R.id.fragment_recipe_form_product_list);

        scenario.recreate();
        scenario.recreate();
        scenario.onFragment(v -> v.onMenuItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        var expected = RecipeAddForm.create(recipe.name(), "", Duration.ZERO,
                List.of(RecipeIngredientToAdd.create(3, selectableData.availableFood().get(1), selectableData.availableUnits().get(1))),
                List.of(RecipeProductToAdd.create(3, selectableData.availableFood().get(1), selectableData.availableUnits().get(1))));
        verify(recipeAddInteractor).add(expected);
        verify(navigator).back();
    }

    @Inject
    public void setRecipeAddInteractor(RecipeAddInteractor recipeAddInteractor) {
        this.recipeAddInteractor = recipeAddInteractor;
    }

    @Inject
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }

    private RecipeAddForm getInput() {
        return RecipeAddForm.create(
                "Pizza",
                "just bake",
                Duration.ofMinutes(4),
                emptyList(),
                emptyList());
    }
}
