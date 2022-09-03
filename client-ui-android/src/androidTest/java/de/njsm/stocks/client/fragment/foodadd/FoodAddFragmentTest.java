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

package de.njsm.stocks.client.fragment.foodadd;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.android.material.textfield.TextInputEditText;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.FoodAddInteractor;
import de.njsm.stocks.client.business.entities.FoodAddForm;
import de.njsm.stocks.client.navigation.Navigator;
import de.njsm.stocks.client.testdata.LocationsForSelection;
import de.njsm.stocks.client.testdata.ScaledUnitsForListing;
import de.njsm.stocks.client.ui.R;
import io.reactivex.rxjava3.core.Observable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.Period;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.client.business.Util.findFirst;
import static de.njsm.stocks.client.fragment.Util.menuItem;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.anything;
import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class FoodAddFragmentTest {

    private FragmentScenario<FoodAddFragment> scenario;

    private FoodAddInteractor foodAddInteractor;

    private Navigator navigator;

    @Before
    public void setup() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        reset(navigator);
        reset(foodAddInteractor);
        when(foodAddInteractor.getUnits()).thenReturn(Observable.just(ScaledUnitsForListing.generate()));
        when(foodAddInteractor.getLocations()).thenReturn(Observable.just(LocationsForSelection.generate()));
        scenario = FragmentScenario.launchInContainer(FoodAddFragment.class, new Bundle(), R.style.StocksTheme);
    }

    @After
    public void tearDown() {
        reset(navigator);
        reset(foodAddInteractor);
    }

    @Test
    public void uiIsShown() {
        onView(allOf(isDescendantOfA(withId(R.id.fragment_food_form_name)), withId(R.id.text_field_conflict_text_field))).check(matches(isDisplayed()));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_food_form_to_buy)), withId(R.id.switch_conflict_switch))).check(matches(isDisplayed()));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_food_form_expiration_offset)), withId(R.id.text_field_conflict_text_field))).check(matches(isDisplayed()));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_food_form_location)), withId(R.id.spinner_conflict_spinner))).check(matches(isDisplayed()));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_food_form_store_unit)), withId(R.id.spinner_conflict_spinner))).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_food_form_description)).check(matches(isDisplayed()));
    }

    @Test
    public void addingFoodIsPropagated() {
        FoodAddForm form = getInput();

        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_food_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(form.name()));
        onView(withId(R.id.fragment_food_form_to_buy)).perform(click());
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_food_form_expiration_offset)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(String.valueOf(form.expirationOffset().getDays())));
        onView(withId(R.id.fragment_food_form_location)).perform(click());
        onData(anything()).atPosition(1 + findFirst(LocationsForSelection.generate(), form.location().get())).perform(click());
        onView(withId(R.id.fragment_food_form_store_unit)).perform(click());
        onData(anything()).atPosition(findFirst(ScaledUnitsForListing.generate(), form.storeUnit())).perform(click());
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_food_form_description)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(form.description()));
        scenario.onFragment(v -> v.onOptionsItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        verify(foodAddInteractor).add(form);
        verify(navigator).back();
    }

    @Test
    public void clearingNameShowsError() {
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_food_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText("some name"), clearText());

        onView(withId(R.id.fragment_food_form_name))
                .check(matches(hasDescendant(withText(R.string.error_may_not_be_empty))));
    }

    @Test
    public void submittingWithoutNameShowsError() {
        scenario.onFragment(v -> v.onOptionsItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        verify(foodAddInteractor, never()).add(any());
        onView(withId(R.id.fragment_food_form_name))
                .check(matches(hasDescendant(withText(R.string.error_may_not_be_empty))));
    }

    @Inject
    public void setFoodAddInteractor(FoodAddInteractor foodAddInteractor) {
        this.foodAddInteractor = foodAddInteractor;
    }

    @Inject
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }

    private FoodAddForm getInput() {
        return FoodAddForm.create(
                "Banana",
                false,
                Period.ofDays(1),
                LocationsForSelection.generate().get(2).id(),
                ScaledUnitsForListing.generate().get(1).id(),
                "they are yellow");
    }
}
