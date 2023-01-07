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

package de.njsm.stocks.client.fragment.searchedfood;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.EntityDeleter;
import de.njsm.stocks.client.business.FoodToBuyInteractor;
import de.njsm.stocks.client.business.SearchInteractor;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.FoodToBuy;
import de.njsm.stocks.client.business.entities.SearchedFoodForListing;
import de.njsm.stocks.client.business.entities.UnitAmount;
import de.njsm.stocks.client.navigation.SearchedFoodNavigator;
import de.njsm.stocks.client.presenter.UnitAmountRenderStrategy;
import de.njsm.stocks.client.ui.R;
import io.reactivex.rxjava3.core.Observable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.client.Matchers.equalBy;
import static de.njsm.stocks.client.Matchers.recyclerView;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SearchedFoodFragmentTest {

    private FragmentScenario<SearchedFoodFragment> scenario;

    private SearchedFoodNavigator navigator;

    private SearchInteractor interactor;

    private FoodToBuyInteractor toBuyInteractor;

    private EntityDeleter<Food> deleter;

    private UnitAmountRenderStrategy renderStrategy;

    @Before
    public void setUp() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);

        String query = "query";
        when(interactor.get(query)).thenReturn(Observable.just(getInput()));
        when(navigator.getQuery(any())).thenReturn(query);

        scenario = FragmentScenario.launchInContainer(SearchedFoodFragment.class, new Bundle(), R.style.StocksTheme);
        renderStrategy = new UnitAmountRenderStrategy();
    }

    @After
    public void tearDown() {
        reset(navigator);
        reset(interactor);
        reset(toBuyInteractor);
    }

    @Test
    public void uiIsShown() {
        int i = 0;
        for (var item : getInput()) {
            onView(recyclerView(R.id.template_swipe_list_list).atPositionOnView(i, R.id.item_food_amount_name))
                    .check(matches(withText(item.name())));
            onView(recyclerView(R.id.template_swipe_list_list).atPositionOnView(i, R.id.item_food_amount_shopping_flag))
                    .check(matches(withEffectiveVisibility(item.toBuy() ? ViewMatchers.Visibility.VISIBLE : ViewMatchers.Visibility.GONE)));
            onView(recyclerView(R.id.template_swipe_list_list).atPositionOnView(i, R.id.item_food_amount_amout))
                    .check(matches(withText(renderStrategy.render(item.storedAmounts()))));
            i++;
        }
    }

    @Test
    public void longClickingNavigatesToEditing() {
        onView(recyclerView(R.id.template_swipe_list_list).atPosition(1))
                .perform(longClick());

        verify(navigator).editFood(getInput().get(1).id());
    }

    @Test
    public void clickingNavigatesToShowing() {
        onView(recyclerView(R.id.template_swipe_list_list).atPosition(1))
                .perform(click());

        verify(navigator).showFood(getInput().get(1).id());
    }

    @Test
    public void rightSwipingDeletesFood() {
        onView(recyclerView(R.id.template_swipe_list_list).atPosition(1))
                .perform(swipeRight());

        verify(deleter).delete(equalBy(getInput().get(1)::id));
    }

    @Test
    public void leftSwipingPutsOnShoppingList() {
        onView(recyclerView(R.id.template_swipe_list_list).atPosition(1)).perform(swipeLeft());

        ArgumentCaptor<FoodToBuy> captor = ArgumentCaptor.forClass(FoodToBuy.class);
        verify(toBuyInteractor).manageFoodToBuy(captor.capture());
        assertThat(captor.getValue().id(), is(getInput().get(1).id()));
        assertThat(captor.getValue().toBuy(), is(true));
    }

    private List<SearchedFoodForListing> getInput() {
        return List.of(
                SearchedFoodForListing.create(1, "Beer", false, List.of(
                        UnitAmount.of(BigDecimal.ONE, "l")
                )),
                SearchedFoodForListing.create(1, "Water", true, List.of(
                        UnitAmount.of(BigDecimal.ZERO, "l")
                ))
        );
    }

    @Inject
    void setNavigator(SearchedFoodNavigator navigator) {
        this.navigator = navigator;
    }

    @Inject
    void setInteractor(SearchInteractor interactor) {
        this.interactor = interactor;
    }

    @Inject
    void setDeleter(EntityDeleter<Food> deleter) {
        this.deleter = deleter;
    }

    @Inject
    void setToBuyInteractor(FoodToBuyInteractor toBuyInteractor) {
        this.toBuyInteractor = toBuyInteractor;
    }
}