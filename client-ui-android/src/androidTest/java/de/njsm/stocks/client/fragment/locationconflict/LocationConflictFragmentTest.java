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

package de.njsm.stocks.client.fragment.locationconflict;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.android.material.textfield.TextInputEditText;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.ErrorRetryInteractor;
import de.njsm.stocks.client.business.FakeLocationConflictInteractor;
import de.njsm.stocks.client.business.entities.ErrorDescription;
import de.njsm.stocks.client.business.entities.LocationEditErrorDetails;
import de.njsm.stocks.client.business.entities.conflict.LocationEditConflictData;
import de.njsm.stocks.client.navigation.LocationConflictNavigator;
import de.njsm.stocks.client.ui.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.client.fragment.Util.menuItem;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LocationConflictFragmentTest {

    private FragmentScenario<LocationConflictFragment> scenario;

    private LocationConflictNavigator navigator;

    private FakeLocationConflictInteractor locationConflictInteractor;

    private ErrorRetryInteractor errorRetryInteractor;

    @Before
    public void setup() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        scenario = FragmentScenario.launchInContainer(LocationConflictFragment.class, new Bundle(), R.style.StocksTheme);
        when(navigator.getErrorId(any(Bundle.class))).thenReturn(42L);
        reset(navigator);
        reset(errorRetryInteractor);
    }

    @After
    public void tearDown() {
        reset(navigator);
        reset(errorRetryInteractor);
    }

    @Test
    public void conflictInBothShowsFullUi() {
        LocationEditConflictData data = LocationEditConflictData.create(1, 42, 43, "name original", "name remote", "name local", "description original", "description remote", "description local");
        locationConflictInteractor.setData(data);

        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_location_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).check(matches(withText(data.name().suggestedValue())));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_location_form_name)), withId(R.id.text_field_conflict_original_content))).check(matches(withText(data.name().original())));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_location_form_name)), withId(R.id.text_field_conflict_remote_content))).check(matches(withText(data.name().remote())));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_location_form_name)), withId(R.id.text_field_conflict_local_content))).check(matches(withText(data.name().local())));
        String mergedDescription = String.format(data.description().suggestedValue(),
                InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext().getString(R.string.hint_original),
                InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext().getString(R.string.hint_remote),
                InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext().getString(R.string.hint_local)
        );
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_location_form_description)),
                withClassName(is(TextInputEditText.class.getName()))
        )).check(matches(withText(mergedDescription)));
    }

    @Test
    public void submissionWorks() {
        LocationEditConflictData data = LocationEditConflictData.create(1, 42, 43, "name original", "name remote", "name local", "description", "description", "description local");
        locationConflictInteractor.setData(data);
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_location_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).check(matches(withText(data.name().suggestedValue())));

        scenario.onFragment(v -> v.onOptionsItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        ArgumentCaptor<ErrorDescription> captor = ArgumentCaptor.forClass(ErrorDescription.class);
        verify(errorRetryInteractor, timeout(1000)).retry(captor.capture());
        LocationEditErrorDetails actual = (LocationEditErrorDetails) captor.getValue().errorDetails();
        assertEquals(data.id(), actual.id());
        assertEquals(data.name().suggestedValue(), actual.name());
        assertEquals(data.description().suggestedValue(), actual.description());
        verify(navigator).back();
    }

    @Test
    public void onlyConflictInNameHidesDescription() {
        LocationEditConflictData data = LocationEditConflictData.create(1, 42, 43, "name original", "name remote", "name local", "description", "description", "description");
        locationConflictInteractor.setData(data);

        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_location_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).check(matches(withText(data.name().suggestedValue())));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_location_form_name)), withId(R.id.text_field_conflict_original_content))).check(matches(withText(data.name().original())));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_location_form_name)), withId(R.id.text_field_conflict_remote_content))).check(matches(withText(data.name().remote())));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_location_form_name)), withId(R.id.text_field_conflict_local_content))).check(matches(withText(data.name().local())));
        onView(withId(R.id.fragment_location_form_description)).check(matches(withEffectiveVisibility(Visibility.GONE)));
    }

    @Test
    public void onlyConflictInDescriptionHidesName() {
        LocationEditConflictData data = LocationEditConflictData.create(1, 42, 43, "name", "name", "name", "description original", "description remote", "description local");
        locationConflictInteractor.setData(data);

        onView(withId(R.id.fragment_location_form_name)).check(matches(withEffectiveVisibility(Visibility.GONE)));
        String mergedDescription = String.format(data.description().suggestedValue(),
                InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext().getString(R.string.hint_original),
                InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext().getString(R.string.hint_remote),
                InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext().getString(R.string.hint_local)
        );
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_location_form_description)),
                withClassName(is(TextInputEditText.class.getName()))
        )).check(matches(withText(mergedDescription)));
    }

    @Test
    public void noChangeSubmitsDirectly() {
        LocationEditConflictData data = LocationEditConflictData.create(1, 42, 43, "name", "name", "name", "description", "description", "description");
        locationConflictInteractor.setData(data);

        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_location_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).check(matches(withText(data.name().suggestedValue())));
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_location_form_description)),
                withClassName(is(TextInputEditText.class.getName()))
        )).check(matches(withText(data.description().suggestedValue())));

        ArgumentCaptor<ErrorDescription> captor = ArgumentCaptor.forClass(ErrorDescription.class);
        verify(errorRetryInteractor, timeout(1000)).retry(captor.capture());
        LocationEditErrorDetails actual = (LocationEditErrorDetails) captor.getValue().errorDetails();
        assertEquals(data.id(), actual.id());
        assertEquals(data.name().suggestedValue(), actual.name());
        assertEquals(data.description().suggestedValue(), actual.description());
        verify(navigator).back();
    }

    @Inject
    void setLocationConflictInteractor(FakeLocationConflictInteractor locationConflictInteractor) {
        this.locationConflictInteractor = locationConflictInteractor;
    }

    @Inject
    void setErrorRetryInteractor(ErrorRetryInteractor errorRetryInteractor) {
        this.errorRetryInteractor = errorRetryInteractor;
    }

    @Inject
    void setNavigator(LocationConflictNavigator navigator) {
        this.navigator = navigator;
    }
}
