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

package de.njsm.stocks.client.fragment.foodedit;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.android.material.textfield.TextInputEditText;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.FakeFoodEditInteractor;
import de.njsm.stocks.client.business.entities.FoodEditingFormData;
import de.njsm.stocks.client.business.entities.FoodToEdit;
import de.njsm.stocks.client.business.entities.LocationForSelection;
import de.njsm.stocks.client.business.entities.ScaledUnitForSelection;
import de.njsm.stocks.client.navigation.FoodEditNavigator;
import de.njsm.stocks.client.presenter.ScaledUnitRenderStrategy;
import de.njsm.stocks.client.testdata.FoodsToEdit;
import de.njsm.stocks.client.testdata.LocationsForSelection;
import de.njsm.stocks.client.testdata.ScaledUnitsForSelection;
import de.njsm.stocks.client.ui.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.Period;
import java.util.Optional;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.client.fragment.Util.menuItem;
import static java.util.Optional.empty;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class FoodEditFragmentTest {

    private FragmentScenario<FoodEditFragment> scenario;

    private FakeFoodEditInteractor interactor;

    private FoodEditNavigator navigator;

    private FoodEditingFormData dataToEdit;

    @Before
    public void setup() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        reset(navigator);
        dataToEdit = FoodsToEdit.generate();
        when(navigator.getId(any(Bundle.class))).thenReturn(dataToEdit);
        interactor.setData(dataToEdit);
        scenario = FragmentScenario.launchInContainer(FoodEditFragment.class, new Bundle(), R.style.StocksTheme);
    }

    @After
    public void tearDown() {
        //verifyNoMoreInteractions(navigator);
        reset(navigator);
        interactor.reset();
    }

    @Test
    public void uiIsShown() {
        ScaledUnitRenderStrategy renderStrategy = new ScaledUnitRenderStrategy();

        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_food_form_name)),
                withClassName(is(TextInputEditText.class.getName()))))
                .check(matches(withText(dataToEdit.name())));
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_food_form_expiration_offset)),
                withClassName(is(TextInputEditText.class.getName()))))
                .check(matches(withText(String.valueOf(dataToEdit.expirationOffset().getDays()))));
        onView(withId(R.id.fragment_food_form_location)).check(matches(allOf(
                isDisplayed(),
                hasDescendant(withText(dataToEdit.locations().get(dataToEdit.currentLocationListPosition().get()).name())))));
        onView(withId(R.id.fragment_food_form_store_unit)).check(matches(allOf(
                isDisplayed(),
                hasDescendant(withText(renderStrategy.render(dataToEdit.storeUnits().get(dataToEdit.currentStoreUnitListPosition()))))
        )));
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_food_form_description)),
                withClassName(is(TextInputEditText.class.getName()))))
                .check(matches(withText(dataToEdit.description())));

        onView(withId(R.id.fragment_food_form_location)).perform(click());
        int index = 1;
        onData(anything()).atPosition(0).check(matches(withText("---")));
        for (LocationForSelection locationListEntry : LocationsForSelection.generate()) {
            onData(anything()).atPosition(index).check(matches(withText(locationListEntry.name())));
            index++;
        }
        onData(anything()).atPosition(dataToEdit.currentLocationListPosition().get()).perform(click());

        onView(withId(R.id.fragment_food_form_store_unit)).perform(click());
        index = 0;
        for (ScaledUnitForSelection unitListEntry : ScaledUnitsForSelection.generate()) {
            onData(anything()).atPosition(index).check(matches(withText(renderStrategy.render(unitListEntry))));
            index++;
        }
        onData(anything()).atPosition(dataToEdit.currentStoreUnitListPosition()).perform(click());

        verify(navigator).getId(any());
    }

    @Test
    public void editingIsPropagated() {
        int unitPosition = 2;
        ScaledUnitForSelection unit = ScaledUnitsForSelection.generate().get(unitPosition);
        FoodToEdit expected = FoodToEdit.create(dataToEdit.id(), "Sausage", Period.ofDays(0), empty(), unit.id(), "German ones");
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_food_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(expected.name()));
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_food_form_expiration_offset)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(String.valueOf(expected.expirationOffset().getDays())));
        onView(withId(R.id.fragment_food_form_store_unit)).perform(click());
        onData(anything()).atPosition(unitPosition).perform(click());
        onView(withId(R.id.fragment_food_form_location)).perform(click());
        onData(anything()).atPosition(0).perform(click());
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_food_form_description)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(expected.description()));

        scenario.onFragment(v -> v.onOptionsItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        assertEquals(Optional.of(expected), interactor.getFormData());
        verify(navigator).getId(any());
        verify(navigator).back();
    }

    @Test
    public void clearingNameShowsError() {
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_food_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText("Banana"), clearText());

        onView(withId(R.id.fragment_food_form_name))
                .check(matches(hasDescendant(withText(R.string.error_may_not_be_empty))));
        verify(navigator).getId(any());
    }

    @Inject
    void setInteractor(FakeFoodEditInteractor interactor) {
        this.interactor = interactor;
    }

    @Inject
    void setNavigator(FoodEditNavigator navigator) {
        this.navigator = navigator;
    }
}
