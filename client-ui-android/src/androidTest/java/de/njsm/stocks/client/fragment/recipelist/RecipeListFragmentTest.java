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

package de.njsm.stocks.client.fragment.recipelist;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.EntityDeleter;
import de.njsm.stocks.client.business.FakeRecipeListInteractor;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.business.entities.Recipe;
import de.njsm.stocks.client.business.entities.RecipeForListing;
import de.njsm.stocks.client.business.entities.RecipesForListing;
import de.njsm.stocks.client.navigation.RecipeListNavigator;
import de.njsm.stocks.client.testdata.RecipeTestData;
import de.njsm.stocks.client.ui.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static de.njsm.stocks.client.Matchers.equalBy;
import static de.njsm.stocks.client.Matchers.recyclerView;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

public class RecipeListFragmentTest {

    private FragmentScenario<RecipeListFragment> scenario;

    private FakeRecipeListInteractor recipeListInteractor;

    private RecipeListNavigator recipeListNavigator;

    private EntityDeleter<Recipe> deleter;

    private Synchroniser synchroniser;

    @Before
    public void setUp() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        scenario = FragmentScenario.launchInContainer(RecipeListFragment.class, new Bundle(), R.style.StocksTheme);
    }

    @After
    public void tearDown() {
        reset(synchroniser);
        reset(recipeListNavigator);
    }

    @Test
    public void dataIsListed() {
        recipeListInteractor.setData(RecipeTestData.generate());

        int position = 0;
        for (RecipeForListing item : RecipeTestData.generate().byName()) {
            onView(recyclerView(R.id.template_swipe_list_list)
                    .atPositionOnView(position, R.id.item_recipe_name))
                    .check(matches(withText(item.name())));
            onView(recyclerView(R.id.template_swipe_list_list)
                    .atPositionOnView(position, R.id.item_recipe_necessary_ingredient_index))
                    .check(matches(withText(String.valueOf(item.necessaryIngredientIndex()))));
            onView(recyclerView(R.id.template_swipe_list_list)
                    .atPositionOnView(position, R.id.item_recipe_sufficient_ingredient_index))
                    .check(matches(withText(String.valueOf(item.sufficientIngredientIndex()))));
            position++;
        }
    }

    @Test
    public void clickingNavigates() {
        int itemIndex = 1;
        RecipesForListing data = RecipeTestData.generate();
        assertTrue("The test wants to access element " + itemIndex, data.byName().size() >= itemIndex + 1);
        RecipeForListing item = data.byName().get(itemIndex);
        assertTrue("Make sure the list position is mapped to an ID by having different values", item.id() != itemIndex);
        recipeListInteractor.setData(data);

        onView(recyclerView(R.id.template_swipe_list_list).atPosition(itemIndex)).perform(click());

        verify(recipeListNavigator).show(item);
    }

    @Test
    public void addingNavigates() {
        onView(withId(R.id.template_swipe_list_fab))
                .perform(click());

        verify(recipeListNavigator).add();
    }

    @Test
    public void swipingRightDeletes() {
        int itemIndex = 1;
        RecipesForListing data = RecipeTestData.generate();
        assertTrue("The test wants to access element " + itemIndex, data.byName().size() >= itemIndex + 1);
        RecipeForListing item = data.byName().get(itemIndex);
        assertTrue("Make sure the list position is mapped to an ID by having different values", item.id() != itemIndex);
        recipeListInteractor.setData(data);

        onView(recyclerView(R.id.template_swipe_list_list).atPosition(itemIndex)).perform(swipeRight());

        verify(deleter).delete(equalBy(item));
    }

    @Inject
    void setRecipeListInteractor(FakeRecipeListInteractor recipeListInteractor) {
        this.recipeListInteractor = recipeListInteractor;
    }

    @Inject
    void setSynchroniser(Synchroniser synchroniser) {
        this.synchroniser = synchroniser;
    }

    @Inject
    void setRecipeListNavigator(RecipeListNavigator recipeListNavigator) {
        this.recipeListNavigator = recipeListNavigator;
    }

    @Inject
    void setDeleter(EntityDeleter<Recipe> deleter) {
        this.deleter = deleter;
    }
}
