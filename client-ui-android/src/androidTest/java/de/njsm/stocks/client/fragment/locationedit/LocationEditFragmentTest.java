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

package de.njsm.stocks.client.fragment.locationedit;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.android.material.textfield.TextInputEditText;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.FakeLocationEditInteractor;
import de.njsm.stocks.client.business.entities.LocationToEdit;
import de.njsm.stocks.client.navigation.LocationEditNavigator;
import de.njsm.stocks.client.testdata.LocationsToEdit;
import de.njsm.stocks.client.ui.R;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.client.fragment.Util.menuItem;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LocationEditFragmentTest {

    private FragmentScenario<LocationEditFragment> scenario;

    private FakeLocationEditInteractor locationEditInteractor;

    private LocationEditNavigator navigator;

    @Before
    public void setup() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        scenario = FragmentScenario.launchInContainer(LocationEditFragment.class, new Bundle(), R.style.StocksTheme);
        when(navigator.getLocationId(any(Bundle.class))).thenReturn(LocationsToEdit.generate().id());
        locationEditInteractor.reset();
    }

    @Test
    public void uiIsShown() {
        LocationToEdit location = LocationsToEdit.generate();

        locationEditInteractor.setData(location);

        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_location_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).check(matches(withText(location.name())));
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_location_form_description)),
                withClassName(is(TextInputEditText.class.getName()))
        )).check(matches(withText(location.description())));
    }

    @Test
    public void clearingNameShowsError() {
        LocationToEdit location = LocationsToEdit.generate();
        locationEditInteractor.setData(location);

        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_location_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(clearText());

        onView(withId(R.id.fragment_location_form_name))
                .check(matches(hasDescendant(withText(R.string.error_may_not_be_empty))));
    }

    @Test
    public void submittingWithoutNameShowsError() {
        LocationToEdit location = LocationsToEdit.generate();
        locationEditInteractor.setData(location);
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_location_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(clearText());

        scenario.onFragment(v -> v.onOptionsItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        onView(withId(R.id.fragment_location_form_name))
                .check(matches(hasDescendant(withText(R.string.error_may_not_be_empty))));
        assertFalse(locationEditInteractor.getFormData().isPresent());
    }

    @Test
    public void submittingWorks() {
        LocationToEdit location = LocationsToEdit.generate();
        locationEditInteractor.setData(location);
        String newName = "new name";
        String newDescription = "new description";
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_location_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(newName));
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_location_form_description)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(newDescription));

        scenario.onFragment(v -> v.onOptionsItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        assertEquals(LocationToEdit.builder()
                .id(location.id())
                .name(newName)
                .description(newDescription)
                .build(),
                locationEditInteractor.getFormData().get()
        );
        verify(navigator).back();
    }

    @Inject
    public void setLocationEditInteractor(FakeLocationEditInteractor locationEditInteractor) {
        this.locationEditInteractor = locationEditInteractor;
    }

    @Inject
    void setNavigator(LocationEditNavigator navigator) {
        this.navigator = navigator;
    }
}
