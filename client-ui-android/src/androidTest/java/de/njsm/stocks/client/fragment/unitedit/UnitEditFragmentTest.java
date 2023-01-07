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

package de.njsm.stocks.client.fragment.unitedit;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.android.material.textfield.TextInputEditText;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.FakeUnitEditInteractor;
import de.njsm.stocks.client.business.entities.UnitToEdit;
import de.njsm.stocks.client.navigation.UnitEditNavigator;
import de.njsm.stocks.client.testdata.UnitsToEdit;
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

public class UnitEditFragmentTest {

    private FragmentScenario<UnitEditFragment> scenario;

    private FakeUnitEditInteractor editInteractor;

    private UnitEditNavigator navigator;

    @Before
    public void setup() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        scenario = FragmentScenario.launchInContainer(UnitEditFragment.class, new Bundle(), R.style.StocksTheme);
        when(navigator.getUnitId(any(Bundle.class))).thenReturn(UnitsToEdit.generate());
        editInteractor.reset();
    }

    @Test
    public void uiIsShown() {
        UnitToEdit unit = UnitsToEdit.generate();

        editInteractor.setData(unit);

        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_unit_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).check(matches(withText(unit.name())));
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_unit_form_abbreviation)),
                withClassName(is(TextInputEditText.class.getName()))
        )).check(matches(withText(unit.abbreviation())));
    }

    @Test
    public void submittingWithoutNameShowsError() {
        UnitToEdit unit = UnitsToEdit.generate();
        editInteractor.setData(unit);
        String newAbbreviation = "new abbreviation";
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_unit_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(clearText());
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_unit_form_abbreviation)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(newAbbreviation));

        scenario.onFragment(v -> v.onOptionsItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        onView(withId(R.id.fragment_unit_form_name))
                .check(matches(hasDescendant(withText(R.string.error_may_not_be_empty))));
        assertFalse(editInteractor.getFormData().isPresent());
    }

    @Test
    public void submittingWithoutAbbreviationShowsError() {
        UnitToEdit unit = UnitsToEdit.generate();
        editInteractor.setData(unit);
        String newName = "new name";
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_unit_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(newName));
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_unit_form_abbreviation)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(clearText());

        scenario.onFragment(v -> v.onOptionsItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        onView(withId(R.id.fragment_unit_form_abbreviation))
                .check(matches(hasDescendant(withText(R.string.error_may_not_be_empty))));
        assertFalse(editInteractor.getFormData().isPresent());
    }

    @Test
    public void submittingWorks() {
        UnitToEdit unit = UnitsToEdit.generate();
        editInteractor.setData(unit);
        String newName = "new name";
        String newAbbreviation = "new abbreviation";
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_unit_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(newName));
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_unit_form_abbreviation)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(newAbbreviation));

        scenario.onFragment(v -> v.onOptionsItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        assertEquals(UnitToEdit.builder()
                .id(unit.id())
                .name(newName)
                .abbreviation(newAbbreviation)
                .build(),
                editInteractor.getFormData().get()
        );
        verify(navigator).back();
    }

    @Inject
    public void setEditInteractor(FakeUnitEditInteractor editInteractor) {
        this.editInteractor = editInteractor;
    }

    @Inject
    void setNavigator(UnitEditNavigator navigator) {
        this.navigator = navigator;
    }
}
