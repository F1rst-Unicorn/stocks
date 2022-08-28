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

package de.njsm.stocks.client.fragment.emptyfood;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.EntityDeleter;
import de.njsm.stocks.client.business.FakeEmptyFoodInteractor;
import de.njsm.stocks.client.business.entities.EmptyFood;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.Identifiable;
import de.njsm.stocks.client.navigation.EmptyFoodNavigator;
import de.njsm.stocks.client.testdata.FoodsForListing;
import de.njsm.stocks.client.ui.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.client.Matchers.recyclerView;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

public class EmptyFoodFragmentTest {

    private FragmentScenario<EmptyFoodFragment> scenario;

    private EmptyFoodNavigator navigator;

    private FakeEmptyFoodInteractor emptyFoodInteractor;

    private EntityDeleter<Food> deleter;

    @Before
    public void setUp() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        scenario = FragmentScenario.launchInContainer(EmptyFoodFragment.class, new Bundle(), R.style.StocksTheme);
    }

    @After
    public void tearDown() {
        reset(navigator);
        reset(deleter);
    }

    @Test
    public void addingFoodNavigates() {
        onView(withId(R.id.template_swipe_list_fab))
                .perform(click());

        verify(navigator).addFood();
    }

    @Test
    public void emptyListShowsText() {
        emptyFoodInteractor.setData(emptyList());

        onView(withId(R.id.template_swipe_list_empty_text))
                .check(matches(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), withText(R.string.hint_no_food))));
    }

    @Test
    public void foodIsListed() {
        emptyFoodInteractor.setData(FoodsForListing.getEmpty());

        int position = 0;
        for (EmptyFood item : FoodsForListing.getEmpty()) {
            onView(recyclerView(R.id.template_swipe_list_list)
                    .atPositionOnView(position, R.id.item_food_outline_name))
                    .check(matches(withText(item.name())));

            Visibility expectedShoppingCartVisibility = item.toBuy() ? Visibility.VISIBLE : Visibility.GONE;
            onView(recyclerView(R.id.template_swipe_list_list)
                    .atPositionOnView(position, R.id.item_food_outline_shopping_flag))
                    .check(matches(withEffectiveVisibility(expectedShoppingCartVisibility)));
            position++;
        }
    }

    @Test
    public void clickingOnItemNavigates() {
        int itemIndex = 1;
        List<EmptyFood> data = FoodsForListing.getEmpty();
        emptyFoodInteractor.setData(data);

        onView(recyclerView(R.id.template_swipe_list_list).atPosition(itemIndex)).perform(click());

        verify(navigator).showFood(data.get(itemIndex).id());
    }

    @Test
    public void longClickingOnItemNavigates() {
        int itemIndex = 1;
        List<EmptyFood> data = FoodsForListing.getEmpty();
        emptyFoodInteractor.setData(data);

        onView(recyclerView(R.id.template_swipe_list_list).atPosition(itemIndex)).perform(longClick());

        verify(navigator).editFood(data.get(itemIndex).id());
    }

    @Test
    public void rightSwipingDeletes() {
        int itemIndex = 1;
        List<EmptyFood> data = FoodsForListing.getEmpty();
        emptyFoodInteractor.setData(data);

        onView(recyclerView(R.id.template_swipe_list_list).atPosition(itemIndex)).perform(swipeRight());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Identifiable<Food>> captor = ArgumentCaptor.forClass(Identifiable.class);
        verify(deleter).delete(captor.capture());
        assertThat(captor.getValue().id(), is(data.get(itemIndex).id()));
    }

    @Inject
    void setNavigator(EmptyFoodNavigator navigator) {
        this.navigator = navigator;
    }

    @Inject
    void setEmptyFoodInteractor(FakeEmptyFoodInteractor emptyFoodInteractor) {
        this.emptyFoodInteractor = emptyFoodInteractor;
    }

    @Inject
    void setDeleter(EntityDeleter<Food> deleter) {
        this.deleter = deleter;
    }
}