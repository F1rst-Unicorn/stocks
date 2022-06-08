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

package de.njsm.stocks.client.fragment.scaledunitconflict;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.android.material.textfield.TextInputEditText;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.ErrorRetryInteractor;
import de.njsm.stocks.client.business.FakeScaledUnitConflictInteractor;
import de.njsm.stocks.client.business.entities.ErrorDescription;
import de.njsm.stocks.client.business.entities.ScaledUnitEditErrorDetails;
import de.njsm.stocks.client.business.entities.UnitForListing;
import de.njsm.stocks.client.business.entities.UnitForSelection;
import de.njsm.stocks.client.business.entities.conflict.ScaledUnitEditConflictData;
import de.njsm.stocks.client.business.entities.conflict.ScaledUnitEditConflictFormData;
import de.njsm.stocks.client.navigation.ScaledUnitConflictNavigator;
import de.njsm.stocks.client.presenter.UnitRenderStrategy;
import de.njsm.stocks.client.ui.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;
import java.math.BigDecimal;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.client.fragment.Util.menuItem;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ScaledUnitConflictFragmentTest {

    private FragmentScenario<ScaledUnitConflictFragment> scenario;

    private ScaledUnitConflictNavigator navigator;

    private FakeScaledUnitConflictInteractor conflictInteractor;

    private ErrorRetryInteractor errorRetryInteractor;

    private UnitRenderStrategy unitRenderStrategy;

    @Before
    public void setup() {
        unitRenderStrategy = new UnitRenderStrategy();
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        conflictInteractor.reset();
        reset(navigator);
        reset(errorRetryInteractor);
        when(navigator.getErrorId(any(Bundle.class))).thenReturn(42L);
        scenario = FragmentScenario.launchInContainer(ScaledUnitConflictFragment.class, new Bundle(), R.style.StocksTheme);
    }

    @After
    public void tearDown() {
        reset(navigator);
        reset(errorRetryInteractor);
    }

    @Test
    public void conflictInBothShowsFullUi() {
        ScaledUnitEditConflictFormData data = ScaledUnitEditConflictFormData.create(
                ScaledUnitEditConflictData.create(1, 2, 3, BigDecimal.valueOf(4), BigDecimal.valueOf(5), BigDecimal.valueOf(6),
                        UnitForListing.create(7, "original name", "original abbreviation"),
                        UnitForListing.create(8, "remote name", "remote abbreviation"),
                        UnitForListing.create(9, "local name", "local abbreviation")
                ),
                singletonList(UnitForSelection.create(8, "remote name"))
        );
        conflictInteractor.setData(data);

        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_scaled_unit_form_scale)),
                withClassName(is(TextInputEditText.class.getName()))
        )).check(matches(withText(data.scale().suggestedValue().toPlainString())));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_scaled_unit_form_scale)), withId(R.id.conflict_labels_original_content))).check(matches(withText(data.scale().original().toPlainString())));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_scaled_unit_form_scale)), withId(R.id.conflict_labels_remote_content))).check(matches(withText(data.scale().remote().toPlainString())));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_scaled_unit_form_scale)), withId(R.id.conflict_labels_local_content))).check(matches(withText(data.scale().local().toPlainString())));
        onView(withId(R.id.fragment_scaled_unit_form_unit)).check(matches(allOf(
                isDisplayed(),
                hasDescendant(withText(data.availableUnits().get(data.currentUnitListPosition()).name()))
        )));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_scaled_unit_form_unit)), withId(R.id.conflict_labels_original_content))).check(matches(withText(unitRenderStrategy.render(data.unit().original()))));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_scaled_unit_form_unit)), withId(R.id.conflict_labels_remote_content))).check(matches(withText(unitRenderStrategy.render(data.unit().remote()))));
        onView(allOf(isDescendantOfA(withId(R.id.fragment_scaled_unit_form_unit)), withId(R.id.conflict_labels_local_content))).check(matches(withText(unitRenderStrategy.render(data.unit().local()))));
    }

    @Test
    public void submissionWorks() {
        ScaledUnitEditConflictFormData data = ScaledUnitEditConflictFormData.create(
                ScaledUnitEditConflictData.create(1, 2, 3, BigDecimal.valueOf(4), BigDecimal.valueOf(5), BigDecimal.valueOf(6),
                        UnitForListing.create(7, "original name", "original abbreviation"),
                        UnitForListing.create(8, "remote name", "remote abbreviation"),
                        UnitForListing.create(9, "local name", "local abbreviation")
                ),
                asList(
                        UnitForSelection.create(8, "remote name"),
                        UnitForSelection.create(9, "local name"))
        );
        conflictInteractor.setData(data);
        waitForUiToAppear(data);

        scenario.onFragment(v -> v.onOptionsItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        ArgumentCaptor<ErrorDescription> captor = ArgumentCaptor.forClass(ErrorDescription.class);
        verify(errorRetryInteractor, timeout(1000)).retry(captor.capture());
        ScaledUnitEditErrorDetails actual = (ScaledUnitEditErrorDetails) captor.getValue().errorDetails();
        assertEquals(data.id(), actual.id());
        assertEquals(data.scale().suggestedValue(), actual.scale());
        assertEquals(data.unit().suggestedValue().id(), actual.unit());
        verify(navigator).back();
    }

    @Test
    public void onlyConflictInScaleHidesUnit() {
        ScaledUnitEditConflictFormData data = ScaledUnitEditConflictFormData.create(
                ScaledUnitEditConflictData.create(1, 2, 3, BigDecimal.valueOf(4), BigDecimal.valueOf(5), BigDecimal.valueOf(6),
                        UnitForListing.create(7, "original name", "original abbreviation"),
                        UnitForListing.create(7, "original name", "original abbreviation"),
                        UnitForListing.create(7, "original name", "original abbreviation")
                ),
                singletonList(UnitForSelection.create(8, "remote name"))
        );
        conflictInteractor.setData(data);

        onView(withId(R.id.fragment_scaled_unit_form_unit)).check(matches(withEffectiveVisibility(Visibility.GONE)));
    }

    @Test
    public void onlyConflictInUnitHidesScale() {
        ScaledUnitEditConflictFormData data = ScaledUnitEditConflictFormData.create(
                ScaledUnitEditConflictData.create(1, 2, 3, BigDecimal.valueOf(4), BigDecimal.valueOf(4), BigDecimal.valueOf(4),
                        UnitForListing.create(7, "original name", "original abbreviation"),
                        UnitForListing.create(8, "remote name", "remote abbreviation"),
                        UnitForListing.create(9, "local name", "local abbreviation")
                ),
                singletonList(UnitForSelection.create(7, "original name"))
        );
        conflictInteractor.setData(data);

        onView(withId(R.id.fragment_scaled_unit_form_scale)).check(matches(withEffectiveVisibility(Visibility.GONE)));
    }

    @Test
    public void noChangeSubmitsDirectly() {
        ScaledUnitEditConflictFormData data = ScaledUnitEditConflictFormData.create(
                ScaledUnitEditConflictData.create(1, 2, 3, BigDecimal.valueOf(4), BigDecimal.valueOf(4), BigDecimal.valueOf(4),
                        UnitForListing.create(7, "original name", "original abbreviation"),
                        UnitForListing.create(7, "original name", "original abbreviation"),
                        UnitForListing.create(7, "original name", "original abbreviation")
                ),
                singletonList(UnitForSelection.create(7, "original name"))
        );
        conflictInteractor.setData(data);

        waitForUiToAppear(data);

        ArgumentCaptor<ErrorDescription> captor = ArgumentCaptor.forClass(ErrorDescription.class);
        verify(errorRetryInteractor, timeout(1000)).retry(captor.capture());
        ScaledUnitEditErrorDetails actual = (ScaledUnitEditErrorDetails) captor.getValue().errorDetails();
        assertEquals(data.id(), actual.id());
        assertEquals(data.scale().suggestedValue(), actual.scale());
        assertEquals(data.unit().suggestedValue().id(), actual.unit());
        verify(navigator).back();
    }

    private void waitForUiToAppear(ScaledUnitEditConflictFormData data) {
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_scaled_unit_form_scale)),
                withClassName(is(TextInputEditText.class.getName()))
        )).check(matches(withText(data.scale().suggestedValue().toPlainString())));
    }

    @Inject
    void setConflictInteractor(FakeScaledUnitConflictInteractor conflictInteractor) {
        this.conflictInteractor = conflictInteractor;
    }

    @Inject
    void setErrorRetryInteractor(ErrorRetryInteractor errorRetryInteractor) {
        this.errorRetryInteractor = errorRetryInteractor;
    }

    @Inject
    void setNavigator(ScaledUnitConflictNavigator navigator) {
        this.navigator = navigator;
    }
}
