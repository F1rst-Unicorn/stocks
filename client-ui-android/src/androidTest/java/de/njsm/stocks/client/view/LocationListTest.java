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

package de.njsm.stocks.client.view;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.ui.R;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class LocationListTest {

    @Test
    public void proofOfConcept() {
        FragmentScenario.launchInContainer(LocationFragment.class, new Bundle(), R.style.StocksTheme);
    }

    @Test
    @Ignore("not yet implemented")
    public void swipingDownCausesARefresh() {
        FragmentScenario<LocationFragment> scenario = FragmentScenario.launchInContainer(LocationFragment.class, new Bundle(), R.style.StocksTheme);

        onView(withId(R.id.template_swipe_list_list)).perform(swipeDown());

        scenario.onFragment(fragment -> {
            Synchroniser synchroniser = ((Application) fragment.requireActivity().getApplication()).getDaggerRoot().synchroniser();
            verify(synchroniser).synchronise();
        });
    }

    @Test
    @Ignore("not yet implemented")
    public void locationsAreListed() {
        FragmentScenario<LocationFragment> scenario = FragmentScenario.launchInContainer(LocationFragment.class, new Bundle(), R.style.StocksTheme);

        onView(withId(R.id.template_swipe_list_list)).check(matches(withChild(withText("Fridge"))));
        onView(withId(R.id.template_swipe_list_list)).check(matches(withChild(withText("Cupboard"))));
    }
}
