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

package de.njsm.stocks.client.fragment.shoppinglist;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.FoodToBuyInteractor;
import de.njsm.stocks.client.business.entities.FoodToBuy;
import de.njsm.stocks.client.business.entities.FoodWithAmountForListing;
import de.njsm.stocks.client.business.entities.UnitAmount;
import de.njsm.stocks.client.navigation.ShoppingListNavigator;
import de.njsm.stocks.client.presenter.UnitAmountRenderStrategy;
import de.njsm.stocks.client.ui.R;
import io.reactivex.rxjava3.core.Observable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.client.Matchers.equalBy;
import static de.njsm.stocks.client.Matchers.recyclerView;
import static org.mockito.Mockito.*;

public class ShoppingListFragmentTest {

    private FragmentScenario<ShoppingListFragment> scenario;

    private ShoppingListNavigator navigator;

    private FoodToBuyInteractor toBuyInteractor;

    private UnitAmountRenderStrategy renderStrategy;

    @Before
    public void setUp() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        when(toBuyInteractor.getFoodToBuy()).thenReturn(Observable.just(getInput()));
        scenario = FragmentScenario.launchInContainer(ShoppingListFragment.class, new Bundle(), R.style.StocksTheme);
        renderStrategy = new UnitAmountRenderStrategy();
    }

    @After
    public void tearDown() {
        reset(navigator);
        reset(toBuyInteractor);
    }

    @Test
    public void uiIsShown() {
        int i = 0;
        for (var item : getInput()) {
            onView(recyclerView(R.id.template_swipe_list_list).atPositionOnView(i, R.id.item_food_amount_name))
                    .check(matches(withText(item.name())));
            onView(recyclerView(R.id.template_swipe_list_list).atPositionOnView(i, R.id.item_food_amount_shopping_flag))
                    .check(matches(withEffectiveVisibility(Visibility.GONE)));
            onView(recyclerView(R.id.template_swipe_list_list).atPositionOnView(i, R.id.item_food_amount_amout))
                    .check(matches(withText(renderStrategy.render(item.storedAmounts()))));
            i++;
        }
    }

    @Test
    public void longClickingNavigatesToEditing() {
        onView(recyclerView(R.id.template_swipe_list_list).atPosition(1))
                .perform(longClick());

        verify(navigator).editFood(equalBy(getInput().get(1)));
    }

    @Test
    public void clickingNavigatesToShowing() {
        onView(recyclerView(R.id.template_swipe_list_list).atPosition(1))
                .perform(click());

        verify(navigator).showFood(equalBy(getInput().get(1)));
    }

    @Test
    public void rightSwipingRemovesFromShoppingList() {
        onView(recyclerView(R.id.template_swipe_list_list).atPosition(1))
                .perform(swipeRight());

        verify(toBuyInteractor).manageFoodToBuy(FoodToBuy.removeFromShoppingList(getInput().get(1).id()));
    }

    private List<FoodWithAmountForListing> getInput() {
        return List.of(
                FoodWithAmountForListing.create(1, "Beer", List.of(
                        UnitAmount.of(BigDecimal.ONE, "l")
                )),
                FoodWithAmountForListing.create(1, "Water", List.of(
                        UnitAmount.of(BigDecimal.ZERO, "l")
                ))
        );
    }

    @Inject
    void setNavigator(ShoppingListNavigator navigator) {
        this.navigator = navigator;
    }

    @Inject
    void setToBuyInteractor(FoodToBuyInteractor toBuyInteractor) {
        this.toBuyInteractor = toBuyInteractor;
    }
}