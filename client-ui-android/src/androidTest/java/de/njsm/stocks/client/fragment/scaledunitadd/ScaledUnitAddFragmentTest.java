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

package de.njsm.stocks.client.fragment.scaledunitadd;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.android.material.textfield.TextInputEditText;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.ScaledUnitAddInteractor;
import de.njsm.stocks.client.business.entities.ScaledUnitAddForm;
import de.njsm.stocks.client.business.entities.UnitForSelection;
import de.njsm.stocks.client.navigation.Navigator;
import de.njsm.stocks.client.testdata.UnitsForSelection;
import de.njsm.stocks.client.ui.R;
import io.reactivex.rxjava3.core.Observable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.math.BigDecimal;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.client.fragment.Util.menuItem;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class ScaledUnitAddFragmentTest {

    private FragmentScenario<ScaledUnitAddFragment> scenario;

    private ScaledUnitAddInteractor scaledUnitAddInteractor;

    private Navigator navigator;

    @Before
    public void setup() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        scenario = FragmentScenario.launchInContainer(ScaledUnitAddFragment.class, new Bundle(), R.style.StocksTheme);
        reset(scaledUnitAddInteractor);
        reset(navigator);
        when(scaledUnitAddInteractor.getUnits()).thenReturn(Observable.just(UnitsForSelection.generate()));
    }

    @After
    public void tearDown() {
        verifyNoMoreInteractions(scaledUnitAddInteractor);
        verifyNoMoreInteractions(navigator);
    }

    @Test
    public void uiIsShown() {
        UnitForSelection unit = UnitsForSelection.generate().get(0);

        onView(withId(R.id.fragment_scaled_unit_form_scale)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_scaled_unit_form_unit)).check(matches(allOf(
                isDisplayed(),
                hasDescendant(withText(unit.name()))
        )));
    }

    @Test
    public void addingScaledUnitIsPropagated() {
        int itemIndex = 1;
        UnitForSelection unit = UnitsForSelection.generate().get(itemIndex);
        ScaledUnitAddForm form = ScaledUnitAddForm.create(BigDecimal.ONE, unit.id());
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_scaled_unit_form_scale)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(form.scale().toPlainString()));
        onView(withId(R.id.fragment_scaled_unit_form_unit)).perform(click());
        onData(anything()).atPosition(itemIndex).perform(click());

        scenario.onFragment(v -> v.onOptionsItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        verify(scaledUnitAddInteractor).add(form);
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
    }

    @Test
    public void submittingWithoutScaleShowsError() {
        scenario.onFragment(v -> v.onOptionsItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        verify(scaledUnitAddInteractor, never()).add(any());
        onView(withId(R.id.fragment_scaled_unit_form_scale))
                .check(matches(hasDescendant(withText(R.string.error_may_not_be_empty))));
    }

    @Inject
    void setScaledUnitAddInteractor(ScaledUnitAddInteractor scaledUnitAddInteractor) {
        this.scaledUnitAddInteractor = scaledUnitAddInteractor;
    }

    @Inject
    void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}
