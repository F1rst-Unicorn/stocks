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

package de.njsm.stocks.client.fragment.fooditemlist;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.Matchers;
import de.njsm.stocks.client.business.*;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.navigation.FoodItemListNavigator;
import de.njsm.stocks.client.presenter.DateRenderStrategy;
import de.njsm.stocks.client.presenter.UnitAmountRenderStrategy;
import de.njsm.stocks.client.testdata.FoodItemsForListing;
import de.njsm.stocks.client.ui.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.client.Matchers.equalBy;
import static de.njsm.stocks.client.Matchers.recyclerView;
import static de.njsm.stocks.client.fragment.Util.menuItem;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FoodItemListFragmentTest {

    private FragmentScenario<FoodItemListFragment> scenario;

    private FakeFoodItemListInteractor foodItemListInteractor;

    private FoodItemListNavigator foodItemListNavigator;

    private FoodToBuyInteractor toBuyInteractor;

    private Synchroniser synchroniser;

    private EntityDeleter<FoodItem> foodItemDeleter;

    private DateRenderStrategy dateRenderStrategy;

    private UnitAmountRenderStrategy unitAmountRenderStrategy;

    private Id<Food> food;

    private Localiser localiser;

    @Before
    public void setUp() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        food = () -> 42;
        when(foodItemListNavigator.getFoodId(any())).thenReturn(food);
        scenario = FragmentScenario.launchInContainer(FoodItemListFragment.class, new Bundle(), R.style.StocksTheme);
        dateRenderStrategy = new DateRenderStrategy(localiser);
        unitAmountRenderStrategy = new UnitAmountRenderStrategy();
    }

    @After
    public void tearDown() {
        reset(foodItemDeleter);
        reset(synchroniser);
        reset(foodItemListNavigator);
        reset(toBuyInteractor);
    }

    @Test
    public void foodItemsAreListed() {
        foodItemListInteractor.setData(FoodItemsForListing.get());

        int position = 0;
        for (FoodItemForListing item : FoodItemsForListing.get()) {
            onView(recyclerView(R.id.template_swipe_list_list)
                    .atPositionOnView(position, R.id.item_food_item_amount))
                    .check(matches(withText(unitAmountRenderStrategy.render(item.amount()))));
            onView(recyclerView(R.id.template_swipe_list_list)
                    .atPositionOnView(position, R.id.item_food_item_location))
                    .check(matches(withText(item.location())));
            onView(recyclerView(R.id.template_swipe_list_list)
                    .atPositionOnView(position, R.id.item_food_item_date))
                    .check(matches(withText(dateRenderStrategy.render(item.eatBy()))));
            onView(recyclerView(R.id.template_swipe_list_list)
                    .atPositionOnView(position, R.id.item_food_item_user))
                    .check(matches(withText(item.buyer())));
            onView(recyclerView(R.id.template_swipe_list_list)
                    .atPositionOnView(position, R.id.item_food_item_device))
                    .check(matches(withText(item.registerer())));
            position++;
        }
    }

    @Test
    public void emptyListShowsText() {
        foodItemListInteractor.setData(emptyList());

        onView(withId(R.id.template_swipe_list_empty_text))
                .check(matches(allOf(withEffectiveVisibility(Visibility.VISIBLE), withText(R.string.hint_no_food_items))));
    }

    @Test
    public void clickingNavigatesToEditing() {
        int itemIndex = 1;
        List<FoodItemForListing> data = FoodItemsForListing.get();
        assertTrue("The test wants to access element " + itemIndex, data.size() >= itemIndex + 1);
        FoodItemForListing item = data.get(itemIndex);
        assertTrue("Make sure the list position is mapped to an ID by having different values", item.id() != itemIndex);
        foodItemListInteractor.setData(data);

        onView(withId(R.id.template_swipe_list_list))
                .perform(actionOnItemAtPosition(itemIndex, click()));

        verify(foodItemListNavigator).edit(equalBy(item));
    }

    @Test
    public void clickingToBuyTriggersShoppingList() {
        List<FoodItemForListing> data = FoodItemsForListing.get();
        foodItemListInteractor.setData(data);

        scenario.onFragment(v -> v.onMenuItemSelected(menuItem(v.requireContext(), R.id.menu_food_items_shopping_list)));

        ArgumentCaptor<FoodToToggleBuy> captor = ArgumentCaptor.forClass(FoodToToggleBuy.class);
        verify(toBuyInteractor).manageFoodToBuy(captor.capture());
        assertEquals(food.id(), captor.getValue().id());
    }

    @Test
    public void clickingEditFoodNavigates() {
        List<FoodItemForListing> data = FoodItemsForListing.get();
        foodItemListInteractor.setData(data);

        scenario.onFragment(v -> v.onMenuItemSelected(menuItem(v.requireContext(), R.id.menu_food_items_edit)));

        verify(foodItemListNavigator).editFood(equalBy(food));
    }

    @Test
    public void clickingEanNumbersNavigates() {
        List<FoodItemForListing> data = FoodItemsForListing.get();
        foodItemListInteractor.setData(data);

        scenario.onFragment(v -> v.onMenuItemSelected(menuItem(v.requireContext(), R.id.menu_food_items_ean_codes)));

        verify(foodItemListNavigator).showEanNumbers(equalBy(food));
    }

    @Test
    public void deletionWorks() {
        List<FoodItemForListing> data = FoodItemsForListing.get();
        assertFalse(data.isEmpty());
        foodItemListInteractor.setData(data);
        int itemIndex = 0;

        onView(withId(R.id.template_swipe_list_list))
                .perform(actionOnItemAtPosition(itemIndex, swipeRight()));

        verify(foodItemDeleter).delete(data.get(itemIndex));
    }

    @Test
    public void addingNavigates() {
        onView(withId(R.id.template_swipe_list_fab))
                .perform(click());

        verify(foodItemListNavigator).add(Matchers.equalBy(food));
    }

    @Inject
    public void setFoodItemListInteractor(FakeFoodItemListInteractor foodItemListInteractor) {
        this.foodItemListInteractor = foodItemListInteractor;
    }

    @Inject
    public void setSynchroniser(Synchroniser synchroniser) {
        this.synchroniser = synchroniser;
    }

    @Inject
    public void setFoodItemListNavigator(FoodItemListNavigator foodItemListNavigator) {
        this.foodItemListNavigator = foodItemListNavigator;
    }

    @Inject
    public void setFoodItemDeleter(EntityDeleter<FoodItem> foodItemDeleter) {
        this.foodItemDeleter = foodItemDeleter;
    }

    @Inject
    void setLocaliser(Localiser localiser) {
        this.localiser = localiser;
    }

    @Inject
    void setToBuyInteractor(FoodToBuyInteractor toBuyInteractor) {
        this.toBuyInteractor = toBuyInteractor;
    }
}
