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

package de.njsm.stocks.client.fragment.foodconflict;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.android.material.textfield.TextInputEditText;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.ErrorRetryInteractor;
import de.njsm.stocks.client.business.FakeFoodConflictInteractor;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.business.entities.conflict.*;
import de.njsm.stocks.client.fragment.TestUtility;
import de.njsm.stocks.client.navigation.FoodConflictNavigator;
import de.njsm.stocks.client.presenter.UnitAmountRenderStrategy;
import de.njsm.stocks.client.ui.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.Period;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.client.fragment.Util.menuItem;
import static java.util.Collections.singletonList;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FoodConflictFragmentTest implements TestUtility {

    private FragmentScenario<FoodConflictFragment> scenario;

    private FoodConflictNavigator navigator;

    private FakeFoodConflictInteractor conflictInteractor;

    private ErrorRetryInteractor errorRetryInteractor;

    private UnitAmountRenderStrategy unitRenderStrategy;

    @Before
    public void setup() {
        unitRenderStrategy = new UnitAmountRenderStrategy();
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        conflictInteractor.reset();
        reset(navigator);
        reset(errorRetryInteractor);
        when(navigator.getErrorId(any(Bundle.class))).thenReturn(42L);
        scenario = FragmentScenario.launchInContainer(FoodConflictFragment.class, new Bundle(), R.style.StocksTheme);
    }

    @After
    public void tearDown() {
        reset(navigator);
        reset(errorRetryInteractor);
    }

    @Test
    public void conflictInBothShowsFullUi() {
        FoodEditConflictFormData data = FoodEditConflictFormData.create(
                FoodEditConflictData.create(1, 2, 3,
                        "original name", "remote name", "local name",
                        true, false, false,
                        Period.ofDays(4), Period.ofDays(5), Period.ofDays(6),
                        of(LocationForListing.create(7, "original location")),
                        of(LocationForListing.create(8, "remote location")),
                        of(LocationForListing.create(9, "local location")),
                        ScaledUnitForListing.create(10, "original", BigDecimal.valueOf(11)),
                        ScaledUnitForListing.create(12, "remote", BigDecimal.valueOf(12)),
                        ScaledUnitForListing.create(13, "local", BigDecimal.valueOf(14)),
                        "original description", "remote description", "local description"),
                singletonList(LocationForSelection.create(7, "Fridge")),
                singletonList(ScaledUnitForSelection.create(10, "g", BigDecimal.TEN))
        );
        conflictInteractor.setData(data);

        checkTextField(R.id.fragment_food_form_name, data.name());
        checkTextField(R.id.fragment_food_form_expiration_offset, data.expirationOffset().map(v -> String.valueOf(v.getDays())));
        checkSpinner(R.id.fragment_food_form_location, data.availableLocations().get(data.currentLocationListPosition().orElse(0)).name(), data.location().map(v -> v.map(LocationForListing::name).orElse("---")));
        checkSpinner(R.id.fragment_food_form_store_unit, unitRenderStrategy.render(data.availableStoreUnits().suggested()), data.storeUnit().map(unitRenderStrategy::render));
        checkMergingTextField(R.id.fragment_food_form_description, data.description());
    }

    @Test
    public void submissionWorks() {
        FoodEditConflictFormData data = FoodEditConflictFormData.create(
                FoodEditConflictData.create(1, 2, 3,
                        "original name", "remote name", "local name",
                        true, false, false,
                        Period.ofDays(4), Period.ofDays(5), Period.ofDays(6),
                        of(LocationForListing.create(7, "original location")),
                        of(LocationForListing.create(8, "remote location")),
                        of(LocationForListing.create(9, "local location")),
                        ScaledUnitForListing.create(10, "original", BigDecimal.valueOf(11)),
                        ScaledUnitForListing.create(12, "remote", BigDecimal.valueOf(12)),
                        ScaledUnitForListing.create(13, "local", BigDecimal.valueOf(14)),
                        "description", "description", "description"),
                singletonList(LocationForSelection.create(9, "Fridge")),
                singletonList(ScaledUnitForSelection.create(13, "g", BigDecimal.TEN))
        );
        conflictInteractor.setData(data);
        waitForUiToAppear(data);

        scenario.onFragment(v -> v.onMenuItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        ArgumentCaptor<ErrorDescription> captor = ArgumentCaptor.forClass(ErrorDescription.class);
        verify(errorRetryInteractor, timeout(1000)).retry(captor.capture());
        FoodEditErrorDetails actual = (FoodEditErrorDetails) captor.getValue().errorDetails();
        assertEquals(data.id(), actual.id());
        assertEquals(data.name().suggestedValue(), actual.name());
        assertEquals(data.toBuy().suggestedValue(), actual.toBuy());
        assertEquals(data.expirationOffset().suggestedValue(), actual.expirationOffset());
        assertEquals(data.location().suggestedValue().map(LocationForListing::id), actual.location());
        assertEquals(data.storeUnit().suggestedValue().id(), actual.storeUnit());
        assertEquals(data.description().suggestedValue(), actual.description());
        verify(navigator).back();
    }

    @Test
    public void onlyConflictInNameHidesOtherFields() {
        FoodEditConflictFormData data = FoodEditConflictFormData.create(
                FoodEditConflictData.create(1, 2, 3,
                        "original name", "remote name", "local name",
                        false, false, false,
                        Period.ofDays(4), Period.ofDays(4), Period.ofDays(4),
                        of(LocationForListing.create(7, "original location")),
                        of(LocationForListing.create(7, "original location")),
                        of(LocationForListing.create(7, "original location")),
                        ScaledUnitForListing.create(10, "original", BigDecimal.valueOf(11)),
                        ScaledUnitForListing.create(10, "original", BigDecimal.valueOf(11)),
                        ScaledUnitForListing.create(10, "original", BigDecimal.valueOf(11)),
                        "original description", "original description", "original description"),
                singletonList(LocationForSelection.create(7, "Fridge")),
                singletonList(ScaledUnitForSelection.create(10, "g", BigDecimal.TEN))
        );
        conflictInteractor.setData(data);

        checkTextField(R.id.fragment_food_form_name, data.name());
        onView(withId(R.id.fragment_food_form_expiration_offset)).check(matches(withEffectiveVisibility(Visibility.GONE)));
        onView(withId(R.id.fragment_food_form_location)).check(matches(withEffectiveVisibility(Visibility.GONE)));
        onView(withId(R.id.fragment_food_form_store_unit)).check(matches(withEffectiveVisibility(Visibility.GONE)));
        onView(withId(R.id.fragment_food_form_description)).check(matches(withEffectiveVisibility(Visibility.GONE)));
    }

    @Test
    public void noChangeSubmitsDirectly() {
        FoodEditConflictFormData data = FoodEditConflictFormData.create(
                FoodEditConflictData.create(1, 2, 3,
                        "original name", "original name", "original name",
                        false, false, false,
                        Period.ofDays(4), Period.ofDays(4), Period.ofDays(4),
                        of(LocationForListing.create(7, "original location")),
                        of(LocationForListing.create(7, "original location")),
                        of(LocationForListing.create(7, "original location")),
                        ScaledUnitForListing.create(10, "original", BigDecimal.valueOf(11)),
                        ScaledUnitForListing.create(10, "original", BigDecimal.valueOf(11)),
                        ScaledUnitForListing.create(10, "original", BigDecimal.valueOf(11)),
                        "original description", "original description", "original description"),
                singletonList(LocationForSelection.create(7, "Fridge")),
                singletonList(ScaledUnitForSelection.create(10, "g", BigDecimal.TEN))
        );
        conflictInteractor.setData(data);

        waitForUiToAppear(data);

        ArgumentCaptor<ErrorDescription> captor = ArgumentCaptor.forClass(ErrorDescription.class);
        verify(errorRetryInteractor, timeout(1000)).retry(captor.capture());
        FoodEditErrorDetails actual = (FoodEditErrorDetails) captor.getValue().errorDetails();
        assertEquals(data.id(), actual.id());
        assertEquals(data.name().suggestedValue(), actual.name());
        assertEquals(data.expirationOffset().suggestedValue(), actual.expirationOffset());
        assertEquals(data.location().suggestedValue().map(LocationForListing::id), actual.location());
        assertEquals(data.storeUnit().suggestedValue().id(), actual.storeUnit());
        assertEquals(data.description().suggestedValue(), actual.description());
        verify(navigator).back();
    }

    private void waitForUiToAppear(FoodEditConflictFormData data) {
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_food_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).check(matches(withText(data.name().suggestedValue())));
    }

    @Inject
    void setConflictInteractor(FakeFoodConflictInteractor conflictInteractor) {
        this.conflictInteractor = conflictInteractor;
    }

    @Inject
    void setErrorRetryInteractor(ErrorRetryInteractor errorRetryInteractor) {
        this.errorRetryInteractor = errorRetryInteractor;
    }

    @Inject
    void setNavigator(FoodConflictNavigator navigator) {
        this.navigator = navigator;
    }
}
