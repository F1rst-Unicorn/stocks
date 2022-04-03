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

package de.njsm.stocks.client.fragment.locationadd;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.android.material.textfield.TextInputEditText;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.LocationAddInteractor;
import de.njsm.stocks.client.business.entities.LocationAddForm;
import de.njsm.stocks.client.navigation.Navigator;
import de.njsm.stocks.client.ui.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.client.fragment.Util.menuItem;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class LocationAddFragmentTest {

    private FragmentScenario<LocationAddFragment> scenario;

    private LocationAddInteractor locationAddInteractor;

    private Navigator navigator;

    @Before
    public void setup() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        scenario = FragmentScenario.launchInContainer(LocationAddFragment.class, new Bundle(), R.style.StocksTheme);
    }

    @After
    public void tearDown() {
        verifyNoMoreInteractions(locationAddInteractor);

        reset(locationAddInteractor);
    }

    @Test
    public void uiIsShown() {
        onView(withId(R.id.fragment_location_form_name)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_location_form_description)).check(matches(isDisplayed()));
    }

    @Test
    public void addingLocationIsPropagated() {
        LocationAddForm form = LocationAddForm.create("name", "description");

        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_location_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(form.name()));
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_location_form_description)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(form.description()));
        scenario.onFragment(v -> v.onOptionsItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        verify(locationAddInteractor).addLocation(form);
        verify(navigator).back();
    }

    @Test
    public void clearingNameShowsError() {
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_location_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText("some name"), clearText());

        onView(withId(R.id.fragment_location_form_name))
                .check(matches(hasDescendant(withText(R.string.error_may_not_be_empty))));
    }

    @Test
    public void submittingWithoutNameShowsError() {
        scenario.onFragment(v -> v.onOptionsItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        verify(locationAddInteractor, never()).addLocation(any());
        onView(withId(R.id.fragment_location_form_name))
                .check(matches(hasDescendant(withText(R.string.error_may_not_be_empty))));
    }

    @Inject
    public void setLocationAddInteractor(LocationAddInteractor locationAddInteractor) {
        this.locationAddInteractor = locationAddInteractor;
    }

    @Inject
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}
