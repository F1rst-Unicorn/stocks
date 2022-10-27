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

package de.njsm.stocks.client.fragment.fooditemadd;

import android.os.Bundle;
import android.widget.DatePicker;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.FoodItemAddInteractor;
import de.njsm.stocks.client.business.Localiser;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.fragment.DialogDisplayer;
import de.njsm.stocks.client.navigation.FoodItemAddNavigator;
import de.njsm.stocks.client.presenter.UnitAmountRenderStrategy;
import de.njsm.stocks.client.testdata.LocationsForSelection;
import de.njsm.stocks.client.testdata.ScaledUnitsForSelection;
import de.njsm.stocks.client.ui.R;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.LocalDate;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.PickerActions.setDate;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.client.Matchers.equalBy;
import static de.njsm.stocks.client.Matchers.matchesDate;
import static de.njsm.stocks.client.fragment.Util.menuItem;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class FoodItemAddFragmentTest {

    private FragmentScenario<FoodItemAddFragment> scenario;

    private FoodItemAddInteractor foodItemAddInteractor;

    private FoodItemAddNavigator navigator;

    private Localiser clock;

    private Id<Food> food;

    private UnitAmountRenderStrategy unitAmountRenderStrategy;

    private DialogDisplayer dialogDisplayer;

    private @NonNull BehaviorSubject<FoodItemAddData> prefilledFormData;

    @Before
    public void setup() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        reset(navigator);
        reset(foodItemAddInteractor);
        reset(dialogDisplayer);
        food = () -> 42;
        when(navigator.getFood(any())).thenReturn(food);
        prefilledFormData = BehaviorSubject.createDefault(getInput());
        when(foodItemAddInteractor.getFormData(equalBy(food))).thenReturn(prefilledFormData);
        scenario = FragmentScenario.launchInContainer(FoodItemAddFragment.class, new Bundle(), R.style.StocksTheme);
        unitAmountRenderStrategy = new UnitAmountRenderStrategy();
    }

    @After
    public void tearDown() {
        reset(navigator);
        reset(foodItemAddInteractor);
        reset(dialogDisplayer);
    }

    @Test
    public void uiIsShown() {
        FoodItemAddData form = getInput();

        onView(allOf(isDescendantOfA(withId(R.id.fragment_food_item_form_date)), withId(R.id.date_conflict_date_picker))).check(matches(
                allOf(isDisplayed(), matchesDate(form.predictedEatBy()))));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_food_item_form_location)), withId(R.id.spinner_conflict_spinner))).check(matches(
                allOf(isDisplayed(), hasDescendant(withText(form.locations().suggested().name())))));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_food_item_form_unit)), withId(R.id.spinner_conflict_spinner))).check(matches(
                allOf(isDisplayed(), hasDescendant(withText(unitAmountRenderStrategy.render(form.scaledUnits().suggested()))))));
    }

    @Test
    public void addingFoodItemIsPropagated() {
        int locationPositionToSelect = 2;
        int unitPositionToSelect = 2;
        FoodItemForm expected = FoodItemForm.create(
                LocalDate.ofEpochDay(7),
                food.id(),
                LocationsForSelection.generate().get(locationPositionToSelect).id(),
                ScaledUnitsForSelection.generate().get(unitPositionToSelect).id()
        );

        onView(withClassName(equalTo(DatePicker.class.getName())))
                .perform(setDate(expected.eatBy().getYear(), expected.eatBy().getMonthValue(), expected.eatBy().getDayOfMonth()));
        onView(withId(R.id.fragment_food_item_form_location)).perform(click());
        onData(anything()).atPosition(locationPositionToSelect).perform(click());
        onView(withId(R.id.fragment_food_item_form_unit)).perform(click());
        onData(anything()).atPosition(unitPositionToSelect).perform(click());
        scenario.onFragment(v -> v.onOptionsItemSelected(menuItem(v.requireContext(), R.id.menu_check_and_continue_check)));

        verify(foodItemAddInteractor).add(expected);
        verify(navigator).back();
    }

    @Test
    public void todayButtonWorks() {
        onView(withId(R.id.date_conflict_today)).perform(click());

        onView(allOf(isDescendantOfA(withId(R.id.fragment_food_item_form_date)), withId(R.id.date_conflict_date_picker))).check(matches(
                allOf(isDisplayed(), matchesDate(clock.today()))));
    }

    @Test
    public void predictButtonWorks() {
        LocalDate expected = getInput().predictedEatBy();
        LocalDate different = expected.plusDays(42);
        onView(withClassName(equalTo(DatePicker.class.getName())))
                .perform(setDate(different.getYear(), different.getMonthValue(), different.getDayOfMonth()));

        onView(withId(R.id.date_conflict_predict)).perform(click());

        onView(allOf(isDescendantOfA(withId(R.id.fragment_food_item_form_date)), withId(R.id.date_conflict_date_picker))).check(matches(
                allOf(isDisplayed(), matchesDate(expected))));
    }

    @Test
    public void severalItemsCanBeAdded() {
        scenario.onFragment(v -> v.onOptionsItemSelected(menuItem(v.requireContext(), R.id.menu_check_and_continue)));

        verify(navigator, never()).back();
    }

    @Test
    public void missingLocationsReturnsWithError() {
        prefilledFormData.onNext(FoodItemAddData.create(
                FoodForSelection.create(food.id(), "Banana"),
                LocalDate.ofEpochDay(5),
                ListWithSuggestion.empty(),
                ListWithSuggestion.create(ScaledUnitsForSelection.generate(), 1)));

        onView(withId(R.id.fragment_food_item_form_unit)).check(matches(isDisplayed()));

        verify(dialogDisplayer).showInformation(R.string.error_add_location_first);
        verify(navigator).back();
    }

    @Inject
    public void setFoodItemAddInteractor(FoodItemAddInteractor foodItemAddInteractor) {
        this.foodItemAddInteractor = foodItemAddInteractor;
    }

    @Inject
    public void setNavigator(FoodItemAddNavigator navigator) {
        this.navigator = navigator;
    }

    @Inject
    public void setClock(Localiser clock) {
        this.clock = clock;
    }

    @Inject
    void setDialogDisplayer(DialogDisplayer dialogDisplayer) {
        this.dialogDisplayer = dialogDisplayer;
    }

    private FoodItemAddData getInput() {
        return FoodItemAddData.create(
                FoodForSelection.create(food.id(), "Banana"),
                LocalDate.ofEpochDay(5),
                ListWithSuggestion.create(LocationsForSelection.generate(), 1),
                ListWithSuggestion.create(ScaledUnitsForSelection.generate(), 1));
    }
}
