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

package de.njsm.stocks.client.fragment.scaledunitedit;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.android.material.textfield.TextInputEditText;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.FakeScaledUnitEditInteractor;
import de.njsm.stocks.client.business.entities.ScaledUnitEditingFormData;
import de.njsm.stocks.client.business.entities.ScaledUnitToEdit;
import de.njsm.stocks.client.business.entities.UnitForSelection;
import de.njsm.stocks.client.navigation.ScaledUnitEditNavigator;
import de.njsm.stocks.client.testdata.ScaledUnitsToEdit;
import de.njsm.stocks.client.testdata.UnitsForSelection;
import de.njsm.stocks.client.ui.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.client.fragment.Util.menuItem;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class ScaledUnitEditFragmentTest {

    private FragmentScenario<ScaledUnitEditFragment> scenario;

    private FakeScaledUnitEditInteractor scaledUnitEditInteractor;

    private ScaledUnitEditNavigator navigator;

    private ScaledUnitEditingFormData dataToEdit;

    @Before
    public void setup() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        reset(navigator);
        dataToEdit = ScaledUnitsToEdit.generate();
        when(navigator.getScaledUnitId(any(Bundle.class))).thenReturn(dataToEdit);
        scaledUnitEditInteractor.setData(dataToEdit);
        scenario = FragmentScenario.launchInContainer(ScaledUnitEditFragment.class, new Bundle(), R.style.StocksTheme);
    }

    @After
    public void tearDown() {
        verifyNoMoreInteractions(navigator);
        reset(navigator);
        scaledUnitEditInteractor.reset();
    }

    @Test
    public void uiIsShown() {
        List<UnitForSelection> units = UnitsForSelection.generate();

        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_scaled_unit_form_scale)),
                withClassName(is(TextInputEditText.class.getName()))))
                .check(matches(withText(dataToEdit.scale().toPlainString())));
        onView(withId(R.id.fragment_scaled_unit_form_unit)).check(matches(allOf(
                isDisplayed(),
                hasDescendant(withText(units.get(dataToEdit.availableUnits().suggestion()).name()))
        )));

        onView(withId(R.id.fragment_scaled_unit_form_unit)).perform(click());

        int index = 0;
        for (UnitForSelection unitListEntry : UnitsForSelection.generate()) {
            onData(anything()).atPosition(index).check(matches(withText(unitListEntry.name())));
            index++;
        }

        verify(navigator).getScaledUnitId(any());
    }

    @Test
    public void editingScaledUnitIsPropagated() {
        int itemIndex = 2;
        UnitForSelection unit = UnitsForSelection.generate().get(itemIndex);
        ScaledUnitToEdit expected = ScaledUnitToEdit.create(dataToEdit.id(), BigDecimal.TEN, unit.id());
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_scaled_unit_form_scale)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(expected.scale().toPlainString()));
        onView(withId(R.id.fragment_scaled_unit_form_unit)).perform(click());
        onData(anything()).atPosition(itemIndex).perform(click());

        scenario.onFragment(v -> v.onOptionsItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        assertEquals(Optional.of(expected), scaledUnitEditInteractor.getFormData());
        verify(navigator).getScaledUnitId(any());
        verify(navigator).back();
    }

    @Test
    public void clearingScaleShowsError() {
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_scaled_unit_form_scale)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText("123"), clearText());

        onView(withId(R.id.fragment_scaled_unit_form_scale))
                .check(matches(hasDescendant(withText(R.string.error_may_not_be_empty))));
        verify(navigator).getScaledUnitId(any());
    }

    @Inject
    void setScaledUnitEditInteractor(FakeScaledUnitEditInteractor scaledUnitEditInteractor) {
        this.scaledUnitEditInteractor = scaledUnitEditInteractor;
    }

    @Inject
    void setNavigator(ScaledUnitEditNavigator navigator) {
        this.navigator = navigator;
    }
}
