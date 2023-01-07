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

package de.njsm.stocks.client.fragment.fooditemedit;

import android.os.Bundle;
import android.widget.DatePicker;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.FakeFoodItemEditInteractor;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.navigation.FoodItemEditNavigator;
import de.njsm.stocks.client.presenter.UnitAmountRenderStrategy;
import de.njsm.stocks.client.testdata.LocationsForSelection;
import de.njsm.stocks.client.testdata.ScaledUnitsForSelection;
import de.njsm.stocks.client.ui.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Optional;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.PickerActions.setDate;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.client.Matchers.matchesDate;
import static de.njsm.stocks.client.fragment.Util.menuItem;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class FoodItemEditFragmentTest {

    private FragmentScenario<FoodItemEditFragment> scenario;

    private FakeFoodItemEditInteractor interactor;

    private FoodItemEditNavigator navigator;

    private FoodItemEditFormData dataToEdit;

    @Before
    public void setup() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        reset(navigator);
        dataToEdit = FoodItemEditFormData.create(
                1,
                FoodForSelection.create(2, "Banana"),
                LocalDate.ofEpochDay(0),
                ListWithSuggestion.create(LocationsForSelection.generate(), 1),
                ListWithSuggestion.create(ScaledUnitsForSelection.generate(), 1)
        );
        when(navigator.getFoodItem(any(Bundle.class))).thenReturn(dataToEdit);
        interactor.setData(dataToEdit);
        scenario = FragmentScenario.launchInContainer(FoodItemEditFragment.class, new Bundle(), R.style.StocksTheme);
    }

    @After
    public void tearDown() {
        reset(navigator);
        interactor.reset();
    }

    @Test
    public void uiIsShown() {
        UnitAmountRenderStrategy unitAmountRenderStrategy = new UnitAmountRenderStrategy();

        onView(allOf(isDescendantOfA(withId(R.id.fragment_food_item_form_date)), withId(R.id.date_conflict_date_picker))).check(matches(
                allOf(isDisplayed(), matchesDate(dataToEdit.eatBy()))));

        onView(withId(R.id.fragment_food_item_form_location)).perform(click());
        int index = 0;
        for (LocationForSelection locationListEntry : LocationsForSelection.generate()) {
            onData(anything()).atPosition(index).check(matches(withText(locationListEntry.name())));
            index++;
        }
        onData(anything()).atPosition(dataToEdit.locations().suggestion()).perform(click());

        onView(withId(R.id.fragment_food_item_form_unit)).perform(click());
        index = 0;
        for (ScaledUnitForSelection unitListEntry : ScaledUnitsForSelection.generate()) {
            onData(anything()).atPosition(index).check(matches(withText(unitAmountRenderStrategy.render(unitListEntry))));
            index++;
        }
        onData(anything()).atPosition(dataToEdit.scaledUnits().suggestion()).perform(click());

        verify(navigator).getFoodItem(any());
    }

    @Test
    public void editingIsPropagated() {
        int unitPosition = 2;
        ScaledUnitForSelection unit = ScaledUnitsForSelection.generate().get(unitPosition);
        int locationPosition = 2;
        LocationForSelection location = LocationsForSelection.generate().get(locationPosition);
        FoodItemToEdit expected = FoodItemToEdit.create(dataToEdit.id(), LocalDate.ofEpochDay(7), location.id(), unit.id());

        onView(withClassName(equalTo(DatePicker.class.getName())))
                .perform(setDate(expected.eatBy().getYear(), expected.eatBy().getMonthValue(), expected.eatBy().getDayOfMonth()));
        onView(withId(R.id.fragment_food_item_form_location)).perform(click());
        onData(anything()).atPosition(locationPosition).perform(click());
        onView(withId(R.id.fragment_food_item_form_unit)).perform(click());
        onData(anything()).atPosition(unitPosition).perform(click());
        scenario.onFragment(v -> v.onOptionsItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        assertEquals(Optional.of(expected), interactor.getFormData());
        verify(navigator).getFoodItem(any());
        verify(navigator).back();
    }

    @Inject
    void setInteractor(FakeFoodItemEditInteractor interactor) {
        this.interactor = interactor;
    }

    @Inject
    void setNavigator(FoodItemEditNavigator navigator) {
        this.navigator = navigator;
    }
}
