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

package de.njsm.stocks.client.fragment.unitconflict;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.android.material.textfield.TextInputEditText;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.ErrorRetryInteractor;
import de.njsm.stocks.client.business.FakeUnitConflictInteractor;
import de.njsm.stocks.client.business.entities.ErrorDescription;
import de.njsm.stocks.client.business.entities.UnitEditErrorDetails;
import de.njsm.stocks.client.business.entities.conflict.UnitEditConflictData;
import de.njsm.stocks.client.navigation.UnitConflictNavigator;
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

public class UnitConflictFragmentTest {

    private FragmentScenario<UnitConflictFragment> scenario;

    private UnitConflictNavigator navigator;

    private FakeUnitConflictInteractor unitConflictInteractor;

    private ErrorRetryInteractor errorRetryInteractor;

    @Before
    public void setup() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        scenario = FragmentScenario.launchInContainer(UnitConflictFragment.class, new Bundle(), R.style.StocksTheme);
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
        UnitEditConflictData data = UnitEditConflictData.create(1, 42, 43, "name original", "name remote", "name local", "abbreviation original", "abbreviation remote", "abbreviation local");
        unitConflictInteractor.setData(data);

        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_unit_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).check(matches(withText(data.name().suggestedValue())));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_unit_form_name)), withId(R.id.text_field_conflict_original_content))).check(matches(withText(data.name().original())));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_unit_form_name)), withId(R.id.text_field_conflict_remote_content))).check(matches(withText(data.name().remote())));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_unit_form_name)), withId(R.id.text_field_conflict_local_content))).check(matches(withText(data.name().local())));
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_unit_form_abbreviation)),
                withClassName(is(TextInputEditText.class.getName()))
        )).check(matches(withText(data.abbreviation().suggestedValue())));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_unit_form_abbreviation)), withId(R.id.text_field_conflict_original_content))).check(matches(withText(data.abbreviation().original())));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_unit_form_abbreviation)), withId(R.id.text_field_conflict_remote_content))).check(matches(withText(data.abbreviation().remote())));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_unit_form_abbreviation)), withId(R.id.text_field_conflict_local_content))).check(matches(withText(data.abbreviation().local())));
    }

    @Test
    public void submissionWorks() {
        UnitEditConflictData data = UnitEditConflictData.create(1, 42, 43, "name original", "name remote", "name local", "abbreviation original", "abbreviation remote", "abbreviation local");
        unitConflictInteractor.setData(data);
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_unit_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).check(matches(withText(data.name().suggestedValue())));

        scenario.onFragment(v -> v.onOptionsItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        ArgumentCaptor<ErrorDescription> captor = ArgumentCaptor.forClass(ErrorDescription.class);
        verify(errorRetryInteractor, timeout(1000)).retry(captor.capture());
        UnitEditErrorDetails actual = (UnitEditErrorDetails) captor.getValue().errorDetails();
        assertEquals(data.id(), actual.id());
        assertEquals(data.name().suggestedValue(), actual.name());
        assertEquals(data.abbreviation().suggestedValue(), actual.abbreviation());
        verify(navigator).back();
    }

    @Test
    public void onlyConflictInNameHidesDescription() {
        UnitEditConflictData data = UnitEditConflictData.create(1, 42, 43, "name original", "name remote", "name local", "abbreviation", "abbreviation", "abbreviation");
        unitConflictInteractor.setData(data);

        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_unit_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).check(matches(withText(data.name().suggestedValue())));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_unit_form_name)), withId(R.id.text_field_conflict_original_content))).check(matches(withText(data.name().original())));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_unit_form_name)), withId(R.id.text_field_conflict_remote_content))).check(matches(withText(data.name().remote())));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_unit_form_name)), withId(R.id.text_field_conflict_local_content))).check(matches(withText(data.name().local())));
        onView(withId(R.id.fragment_unit_form_abbreviation)).check(matches(withEffectiveVisibility(Visibility.GONE)));
    }

    @Test
    public void onlyConflictInAbbreviationHidesDescription() {
        UnitEditConflictData data = UnitEditConflictData.create(1, 42, 43, "name", "name", "name", "abbreviation original", "abbreviation remote", "abbreviation local");
        unitConflictInteractor.setData(data);

        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_unit_form_abbreviation)),
                withClassName(is(TextInputEditText.class.getName()))
        )).check(matches(withText(data.abbreviation().suggestedValue())));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_unit_form_abbreviation)), withId(R.id.text_field_conflict_original_content))).check(matches(withText(data.abbreviation().original())));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_unit_form_abbreviation)), withId(R.id.text_field_conflict_remote_content))).check(matches(withText(data.abbreviation().remote())));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_unit_form_abbreviation)), withId(R.id.text_field_conflict_local_content))).check(matches(withText(data.abbreviation().local())));
        onView(withId(R.id.fragment_unit_form_name)).check(matches(withEffectiveVisibility(Visibility.GONE)));
    }

    @Test
    public void noChangeSubmitsDirectly() {
        UnitEditConflictData data = UnitEditConflictData.create(1, 42, 43, "name", "name", "name", "abbreviation", "abbreviation", "abbreviation");
        unitConflictInteractor.setData(data);

        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_unit_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).check(matches(withText(data.name().suggestedValue())));
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_unit_form_abbreviation)),
                withClassName(is(TextInputEditText.class.getName()))
        )).check(matches(withText(data.abbreviation().suggestedValue())));

        ArgumentCaptor<ErrorDescription> captor = ArgumentCaptor.forClass(ErrorDescription.class);
        verify(errorRetryInteractor, timeout(1000)).retry(captor.capture());
        UnitEditErrorDetails actual = (UnitEditErrorDetails) captor.getValue().errorDetails();
        assertEquals(data.id(), actual.id());
        assertEquals(data.name().suggestedValue(), actual.name());
        assertEquals(data.abbreviation().suggestedValue(), actual.abbreviation());
        verify(navigator).back();
    }

    @Inject
    void setUnitConflictInteractor(FakeUnitConflictInteractor unitConflictInteractor) {
        this.unitConflictInteractor = unitConflictInteractor;
    }

    @Inject
    void setErrorRetryInteractor(ErrorRetryInteractor errorRetryInteractor) {
        this.errorRetryInteractor = errorRetryInteractor;
    }

    @Inject
    void setNavigator(UnitConflictNavigator navigator) {
        this.navigator = navigator;
    }
}
