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
import de.njsm.stocks.client.business.LocationDeleter;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.business.entities.LocationForListing;
import de.njsm.stocks.client.navigation.LocationListNavigator;
import de.njsm.stocks.client.testdata.LocationsForListing;
import de.njsm.stocks.client.ui.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class LocationListFragmentTest {

    private FragmentScenario<LocationListFragment> scenario;

    private FakeLocationListInteractor locationListInteractor;

    private LocationListNavigator mockLocationListNavigator;

    private Synchroniser synchroniser;

    private LocationDeleter locationDeleter;

    @Before
    public void setUp() {
        scenario = FragmentScenario.launchInContainer(LocationListFragment.class, new Bundle(), R.style.StocksTheme);
        scenario.onFragment(fragment -> ((Application) fragment.requireActivity().getApplication()).getDaggerRoot().inject(this));
    }

    @Test
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

    @Test
    public void clickingALocationNavigates() {
        int itemIndex = 1;
        List<LocationForListing> data = LocationsForListing.getData();
        assertTrue("The test wants to access element " + itemIndex, data.size() >= itemIndex + 1);
        LocationForListing location = data.get(itemIndex);
        assertTrue("Make sure the list position is mapped to an ID by having different values", location.id() != itemIndex);
        locationListInteractor.setData(data);

        onView(withId(R.id.template_swipe_list_list))
                .perform(actionOnItemAtPosition(itemIndex, click()));

        verify(mockLocationListNavigator).showLocation(location.id());
    }

    @Test
    public void locationDeletionWorks() {
        List<LocationForListing> data = LocationsForListing.getData();
        assertFalse(data.isEmpty());
        locationListInteractor.setData(data);
        int itemIndex = 0;

        onView(withId(R.id.template_swipe_list_list))
                .perform(actionOnItemAtPosition(itemIndex, swipeRight()));

        verify(locationDeleter).deleteLocation(data.get(itemIndex));
    }

    @Test
    public void locationAddingNavigates() {
        onView(withId(R.id.template_swipe_list_fab))
                .perform(click());

        verify(mockLocationListNavigator).addLocation();
    }

    @Inject
    public void setLocationListInteractor(FakeLocationListInteractor locationListInteractor) {
        this.locationListInteractor = locationListInteractor;
    }

    @Inject
    public void setSynchroniser(Synchroniser synchroniser) {
        this.synchroniser = synchroniser;
    }

    @Inject
    public void setMockLocationListNavigator(LocationListNavigator mockLocationListNavigator) {
        this.mockLocationListNavigator = mockLocationListNavigator;
    }

    @Inject
    public void setLocationDeleter(LocationDeleter locationDeleter) {
        this.locationDeleter = locationDeleter;
    }
}
