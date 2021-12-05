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
import de.njsm.stocks.client.business.entities.LocationForListing;
import de.njsm.stocks.client.testdata.LocationsForListing;
import de.njsm.stocks.client.ui.R;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class LocationListTest {

    private FragmentScenario<LocationListFragment> scenario;

    private FakeLocationListInteractor locationListInteractor;

    private Synchroniser synchroniser;

    @Before
    public void setUp() {
        scenario = FragmentScenario.launchInContainer(LocationListFragment.class, new Bundle(), R.style.StocksTheme);
        scenario.onFragment(fragment -> ((Application) fragment.requireActivity().getApplication()).getDaggerRoot().inject(this));
    }

    @Test
    @Ignore("not yet implemented")
    public void swipingDownCausesARefresh() {

        onView(withId(R.id.template_swipe_list_list)).perform(swipeDown());

        verify(synchroniser).synchronise();
    }

    @Test
    public void locationsAreListed() {
        locationListInteractor.setData(LocationsForListing.getData());

        for (LocationForListing item : LocationsForListing.getData()) {
            onView(withId(R.id.template_swipe_list_list))
                    .check(matches(withChild(withText(item.name()))));
        }
    }

    @Inject
    public void setLocationListInteractor(FakeLocationListInteractor locationListInteractor) {
        this.locationListInteractor = locationListInteractor;
    }

    @Inject
    public void setSynchroniser(Synchroniser synchroniser) {
        this.synchroniser = synchroniser;
    }
}
