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

package de.njsm.stocks.client.fragment.fooditemconflict;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.ErrorRetryInteractor;
import de.njsm.stocks.client.business.FakeFoodItemConflictInteractor;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.business.entities.conflict.FoodItemEditConflictData;
import de.njsm.stocks.client.business.entities.conflict.FoodItemEditConflictFormData;
import de.njsm.stocks.client.fragment.TestUtility;
import de.njsm.stocks.client.navigation.FoodItemConflictNavigator;
import de.njsm.stocks.client.presenter.DateRenderStrategy;
import de.njsm.stocks.client.presenter.UnitAmountRenderStrategy;
import de.njsm.stocks.client.ui.R;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.client.Matchers.matchesDate;
import static de.njsm.stocks.client.fragment.Util.menuItem;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FoodItemConflictFragmentTest implements TestUtility {

    private FragmentScenario<FoodItemConflictFragment> scenario;

    private FoodItemConflictNavigator navigator;

    private FakeFoodItemConflictInteractor conflictInteractor;

    private ErrorRetryInteractor errorRetryInteractor;

    private UnitAmountRenderStrategy unitRenderStrategy;

    private DateRenderStrategy dateRenderStrategy;

    @Before
    public void setup() {
        unitRenderStrategy = new UnitAmountRenderStrategy();
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        conflictInteractor.reset();
        reset(navigator);
        reset(errorRetryInteractor);
        when(navigator.getErrorId(any(Bundle.class))).thenReturn(42L);
        scenario = FragmentScenario.launchInContainer(FoodItemConflictFragment.class, new Bundle(), R.style.StocksTheme);
    }

    @After
    public void tearDown() {
        reset(navigator);
        reset(errorRetryInteractor);
    }

    @Test
    public void conflictInBothShowsFullUi() {
        FoodItemEditConflictFormData data = FoodItemEditConflictFormData.create(
                FoodItemEditConflictData.create(1, 2, 3, "original name",
                        LocalDate.ofEpochDay(1),
                        LocalDate.ofEpochDay(2),
                        LocalDate.ofEpochDay(3),
                        LocationForListing.create(7, 45, "original location"),
                        LocationForListing.create(8, 46, "remote location"),
                        LocationForListing.create(9, 47, "local location"),
                        ScaledUnitForListing.create(10, "original", BigDecimal.valueOf(11)),
                        ScaledUnitForListing.create(12, "remote", BigDecimal.valueOf(12)),
                        ScaledUnitForListing.create(13, "local", BigDecimal.valueOf(14))),
                singletonList(LocationForSelection.create(9, "Fridge")),
                singletonList(ScaledUnitForSelection.create(13, "g", BigDecimal.TEN))
        );
        conflictInteractor.setData(data);

        onView(allOf(isDescendantOfA(withId(R.id.fragment_food_item_form_date)), withId(R.id.date_conflict_date_picker)))
                .check(matches(matchesDate(data.eatBy().suggestedValue())));
        checkConflictFields(R.id.fragment_food_item_form_date, data.eatBy().map(dateRenderStrategy::render));
        checkSpinner(R.id.fragment_food_item_form_location, data.locations().suggested().name(), data.location().map(LocationForListing::name));
        checkSpinner(R.id.fragment_food_item_form_unit, unitRenderStrategy.render(data.units().suggested()), data.unit().map(unitRenderStrategy::render));
    }

    @Test
    public void submissionWorks() {
        FoodItemEditConflictFormData data = FoodItemEditConflictFormData.create(
                FoodItemEditConflictData.create(1, 2, 3, "original name",
                        LocalDate.ofEpochDay(1),
                        LocalDate.ofEpochDay(2),
                        LocalDate.ofEpochDay(3),
                        LocationForListing.create(7, 45, "original location"),
                        LocationForListing.create(8, 46, "remote location"),
                        LocationForListing.create(9, 47, "local location"),
                        ScaledUnitForListing.create(10, "original", BigDecimal.valueOf(11)),
                        ScaledUnitForListing.create(12, "remote", BigDecimal.valueOf(12)),
                        ScaledUnitForListing.create(13, "local", BigDecimal.valueOf(14))),
                singletonList(LocationForSelection.create(9, "Fridge")),
                singletonList(ScaledUnitForSelection.create(13, "g", BigDecimal.TEN))
        );
        conflictInteractor.setData(data);
        waitForUiToAppear();

        scenario.onFragment(v -> v.onOptionsItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        ArgumentCaptor<ErrorDescription> captor = ArgumentCaptor.forClass(ErrorDescription.class);
        verify(errorRetryInteractor, timeout(1000)).retry(captor.capture());
        FoodItemEditErrorDetails actual = (FoodItemEditErrorDetails) captor.getValue().errorDetails();
        assertEquals(data.id(), actual.id());
        assertEquals(data.eatBy().suggestedValue(), actual.eatBy());
        assertEquals(data.location().suggestedValue().id(), actual.storedIn());
        assertEquals(data.unit().suggestedValue().id(), actual.unit());
        verify(navigator).back();
    }

    @Test
    public void onlyConflictInDateHidesOtherFields() {
        FoodItemEditConflictFormData data = FoodItemEditConflictFormData.create(
                FoodItemEditConflictData.create(1, 2, 3, "original name",
                        LocalDate.ofEpochDay(1),
                        LocalDate.ofEpochDay(2),
                        LocalDate.ofEpochDay(3),
                        LocationForListing.create(7, 45, "original location"),
                        LocationForListing.create(7, 45, "original location"),
                        LocationForListing.create(7, 45, "original location"),
                        ScaledUnitForListing.create(10, "original", BigDecimal.valueOf(11)),
                        ScaledUnitForListing.create(10, "original", BigDecimal.valueOf(11)),
                        ScaledUnitForListing.create(10, "original", BigDecimal.valueOf(11))),
                singletonList(LocationForSelection.create(7, "Fridge")),
                singletonList(ScaledUnitForSelection.create(10, "g", BigDecimal.TEN))
        );
        conflictInteractor.setData(data);

        onView(CoreMatchers.allOf(isDescendantOfA(withId(R.id.fragment_food_item_form_date)), withId(R.id.date_conflict_date_picker)))
                .check(matches(matchesDate(data.eatBy().suggestedValue())));
        checkConflictFields(R.id.fragment_food_item_form_date, data.eatBy().map(dateRenderStrategy::render));
        onView(withId(R.id.fragment_food_item_form_location)).check(matches(withEffectiveVisibility(Visibility.GONE)));
        onView(withId(R.id.fragment_food_item_form_unit)).check(matches(withEffectiveVisibility(Visibility.GONE)));
    }

    @Test
    public void noChangeSubmitsDirectly() {
        FoodItemEditConflictFormData data = FoodItemEditConflictFormData.create(
                FoodItemEditConflictData.create(1, 2, 3, "original name",
                        LocalDate.ofEpochDay(1),
                        LocalDate.ofEpochDay(1),
                        LocalDate.ofEpochDay(1),
                        LocationForListing.create(7, 45, "original location"),
                        LocationForListing.create(7, 45, "original location"),
                        LocationForListing.create(7, 45, "original location"),
                        ScaledUnitForListing.create(10, "original", BigDecimal.valueOf(11)),
                        ScaledUnitForListing.create(10, "original", BigDecimal.valueOf(11)),
                        ScaledUnitForListing.create(10, "original", BigDecimal.valueOf(11))),
                singletonList(LocationForSelection.create(7, "Fridge")),
                singletonList(ScaledUnitForSelection.create(10, "g", BigDecimal.TEN))
        );
        conflictInteractor.setData(data);

        waitForUiToAppear();

        ArgumentCaptor<ErrorDescription> captor = ArgumentCaptor.forClass(ErrorDescription.class);
        verify(errorRetryInteractor, timeout(1000)).retry(captor.capture());
        FoodItemEditErrorDetails actual = (FoodItemEditErrorDetails) captor.getValue().errorDetails();
        assertEquals(data.id(), actual.id());
        assertEquals(data.eatBy().suggestedValue(), actual.eatBy());
        assertEquals(data.location().suggestedValue().id(), actual.storedIn());
        assertEquals(data.unit().suggestedValue().id(), actual.unit());
        verify(navigator).back();
    }

    private void waitForUiToAppear() {
        onView(withId(R.id.fragment_food_item_form_date))
                .check(matches(isDisplayed()));
    }

    @Inject
    void setConflictInteractor(FakeFoodItemConflictInteractor conflictInteractor) {
        this.conflictInteractor = conflictInteractor;
    }

    @Inject
    void setErrorRetryInteractor(ErrorRetryInteractor errorRetryInteractor) {
        this.errorRetryInteractor = errorRetryInteractor;
    }

    @Inject
    void setNavigator(FoodItemConflictNavigator navigator) {
        this.navigator = navigator;
    }

    @Inject
    void setDateRenderStrategy(DateRenderStrategy dateRenderStrategy) {
        this.dateRenderStrategy = dateRenderStrategy;
    }
}
