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

package de.njsm.stocks.client.fragment.outline;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.navigation.OutlineNavigator;
import de.njsm.stocks.client.ui.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class OutlineFragmentTest {

    private FragmentScenario<OutlineFragment> scenario;

    private Synchroniser synchroniser;

    private OutlineNavigator outlineNavigator;

    @Before
    public void setUp() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        reset(synchroniser);
        reset(outlineNavigator);
        scenario = FragmentScenario.launchInContainer(OutlineFragment.class, new Bundle(), R.style.StocksTheme);
    }

    @After
    public void tearDown() {
        reset(synchroniser);
        reset(outlineNavigator);
    }

    @Test
    public void synchronisesOnStartup() {
        onView(withId(R.id.fragment_outline_fab)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        verify(synchroniser, timeout(1000)).synchronise();
    }

    @Test
    public void clickingAllFoodCardNavigates() {
        onView(withId(R.id.fragment_outline_cardview)).perform(click());

        verify(outlineNavigator).showAllFood();
    }

    @Test
    public void clickingEmptyFoodCardNavigates() {
        onView(withId(R.id.fragment_outline_cardview2)).perform(click());

        verify(outlineNavigator).showEmptyFood();
    }

    @Test
    public void clickingAddFoodCardNavigates() {
        onView(withId(R.id.fragment_outline_fab)).perform(click());

        verify(outlineNavigator).addFood();
    }

    @Inject
    void setSynchroniser(Synchroniser synchroniser) {
        this.synchroniser = synchroniser;
    }

    @Inject
    void setOutlineNavigator(OutlineNavigator outlineNavigator) {
        this.outlineNavigator = outlineNavigator;
    }
}
