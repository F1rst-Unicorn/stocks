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

package de.njsm.stocks.client.fragment.errordetails;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.ErrorRetryInteractor;
import de.njsm.stocks.client.business.FakeErrorListInteractor;
import de.njsm.stocks.client.business.entities.ErrorDescription;
import de.njsm.stocks.client.business.entities.LocationAddForm;
import de.njsm.stocks.client.business.entities.StatusCode;
import de.njsm.stocks.client.navigation.ErrorDetailsNavigator;
import de.njsm.stocks.client.ui.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static de.njsm.stocks.client.fragment.Util.menuItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ErrorDetailsFragmentTest {

    private FragmentScenario<ErrorDetailsFragment> scenario;

    private FakeErrorListInteractor errorListInteractor;

    private ErrorDetailsNavigator errorDetailsNavigator;

    private ErrorRetryInteractor errorRetryInteractor;

    @Before
    public void setUp() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        scenario = FragmentScenario.launchInContainer(ErrorDetailsFragment.class, new Bundle(), R.style.StocksTheme);
        when(errorDetailsNavigator.readArguments(any(Bundle.class))).thenReturn(42L);
    }

    @After
    public void tearDown() {
        reset(errorDetailsNavigator);
        reset(errorRetryInteractor);
    }

    @Test
    public void errorDetailsAreShown() {
        LocationAddForm details = LocationAddForm.create("Fridge", "The cold one");
        ErrorDescription description = ErrorDescription.create(3, StatusCode.GENERAL_ERROR, "message", "stacktrace", details);

        errorListInteractor.setData(description);

        onView(withId(R.id.fragment_error_details_data))
                .check(matches(withText(details.name() + "\n" + details.description())));
        onView(withId(R.id.fragment_error_details_status_code))
                .check(matches(withText(R.string.statuscode_general_error_error_list)));
        onView(withId(R.id.fragment_error_details_error_message))
                .check(matches(withText(description.errorMessage())));
        onView(withId(R.id.fragment_error_details_stacktrace))
                .check(matches(withText(description.stackTrace())));
    }

    @Test
    public void pressingRetryDispatchesToBackend() {
        LocationAddForm details = LocationAddForm.create("Fridge", "The cold one");
        ErrorDescription description = ErrorDescription.create(3, StatusCode.GENERAL_ERROR, "message", "stacktrace", details);
        errorListInteractor.setData(description);

        scenario.onFragment(f -> f.onOptionsItemSelected(menuItem(f.getContext(), R.id.menu_error_details_retry)));

        verify(errorRetryInteractor).retry(description);
        verify(errorDetailsNavigator).back();
    }

    @Test
    public void pressingDeleteDispatchesToBackend() {
        LocationAddForm details = LocationAddForm.create("Fridge", "The cold one");
        ErrorDescription description = ErrorDescription.create(3, StatusCode.GENERAL_ERROR, "message", "stacktrace", details);
        errorListInteractor.setData(description);

        scenario.onFragment(f -> f.onOptionsItemSelected(menuItem(f.getContext(), R.id.menu_error_details_delete)));

        verify(errorRetryInteractor).delete(description);
        verify(errorDetailsNavigator).back();
    }

    @Inject
    public void setErrorListInteractor(FakeErrorListInteractor errorListInteractor) {
        this.errorListInteractor = errorListInteractor;
    }

    @Inject
    public void setErrorDetailsNavigator(ErrorDetailsNavigator errorDetailsNavigator) {
        this.errorDetailsNavigator = errorDetailsNavigator;
    }

    @Inject
    public void setErrorRetryInteractor(ErrorRetryInteractor errorRetryInteractor) {
        this.errorRetryInteractor = errorRetryInteractor;
    }
}
