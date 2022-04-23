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

package de.njsm.stocks.client.fragment.errorlist;


import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.ErrorRetryInteractor;
import de.njsm.stocks.client.business.FakeErrorListInteractor;
import de.njsm.stocks.client.business.entities.ErrorDescription;
import de.njsm.stocks.client.business.entities.LocationAddForm;
import de.njsm.stocks.client.business.entities.LocationEditErrorDetails;
import de.njsm.stocks.client.business.entities.StatusCode;
import de.njsm.stocks.client.navigation.ErrorListNavigator;
import de.njsm.stocks.client.ui.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

public class ErrorListFragmentTest {

    private FragmentScenario<ErrorListFragment> scenario;

    private FakeErrorListInteractor errorListInteractor;

    private ErrorRetryInteractor errorRetryInteractor;

    private ErrorListNavigator errorListNavigator;

    @Before
    public void setUp() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        scenario = FragmentScenario.launchInContainer(ErrorListFragment.class, new Bundle(), R.style.StocksTheme);
    }

    @After
    public void tearDown() {
        reset(errorRetryInteractor);
    }

    @Test
    public void emptyListShowsText() {
        errorListInteractor.setData(Collections.emptyList());

        onView(withId(R.id.template_swipe_list_empty_text))
                .check(matches(allOf(withEffectiveVisibility(Visibility.VISIBLE), withText(R.string.text_no_errors))));
    }

    @Test
    public void locationAddErrorIsListed() {
        LocationAddForm locationAddForm = LocationAddForm.create("Fridge", "The cold one");
        ErrorDescription errorDescription = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "", locationAddForm);
        List<ErrorDescription> errors = Arrays.asList(errorDescription);

        errorListInteractor.setData(errors);

        onView(withId(R.id.template_swipe_list_list))
                .check(matches(withChild(withChild(withText(R.string.statuscode_database_unreachable_error_list)))));
        onView(withId(R.id.template_swipe_list_list))
                .check(matches(withChild(withChild(withText(R.string.error_details_location_add_error_list)))));
        onView(withId(R.id.template_swipe_list_list))
                .check(matches(withChild(withChild(withText(locationAddForm.name() + "\n" + locationAddForm.description())))));
    }

    @Test
    public void swipingListItemRetriesTheErroredAction() {
        LocationAddForm locationAddForm = LocationAddForm.create("Fridge", "The cold one");
        ErrorDescription errorDescription = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "", locationAddForm);
        List<ErrorDescription> errors = Arrays.asList(errorDescription);
        errorListInteractor.setData(errors);

        onView(withId(R.id.template_swipe_list_list))
                .perform(actionOnItemAtPosition(0, swipeRight()));

        verify(errorRetryInteractor).retry(errorDescription);
    }

    @Test
    public void swipingListItemLeftDeletesTheError() {
        LocationAddForm locationAddForm = LocationAddForm.create("Fridge", "The cold one");
        ErrorDescription errorDescription = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "", locationAddForm);
        List<ErrorDescription> errors = Arrays.asList(errorDescription);
        errorListInteractor.setData(errors);

        onView(withId(R.id.template_swipe_list_list))
                .perform(actionOnItemAtPosition(0, swipeLeft()));

        verify(errorRetryInteractor).delete(errorDescription);
    }

    @Test
    public void clickingListItemNavigates() {
        LocationAddForm locationAddForm = LocationAddForm.create("Fridge", "The cold one");
        ErrorDescription errorDescription = ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "", locationAddForm);
        List<ErrorDescription> errors = Arrays.asList(errorDescription);
        errorListInteractor.setData(errors);

        onView(withId(R.id.template_swipe_list_list))
                .perform(actionOnItemAtPosition(0, click()));

        verify(errorListNavigator).showErrorDetails(errorDescription.id());
    }

    @Test
    public void clickingLocationEditConflictNavigatesToConflictFragment() {
        LocationEditErrorDetails details = LocationEditErrorDetails.create(3, "name", "description");
        ErrorDescription errorDescription = ErrorDescription.create(1, StatusCode.INVALID_DATA_VERSION, "", "", details);
        List<ErrorDescription> errors = Arrays.asList(errorDescription);
        errorListInteractor.setData(errors);

        onView(withId(R.id.template_swipe_list_list))
                .perform(actionOnItemAtPosition(0, click()));

        verify(errorListNavigator).resolveLocationEditConflict(errorDescription.id());
    }

    @Test
    public void retryingLocationEditConflictNavigatesToConflictFragment() {
        LocationEditErrorDetails details = LocationEditErrorDetails.create(3, "name", "description");
        ErrorDescription errorDescription = ErrorDescription.create(1, StatusCode.INVALID_DATA_VERSION, "", "", details);
        List<ErrorDescription> errors = Arrays.asList(errorDescription);
        errorListInteractor.setData(errors);

        onView(withId(R.id.template_swipe_list_list))
                .perform(actionOnItemAtPosition(0, swipeLeft()));

        verify(errorListNavigator).resolveLocationEditConflict(errorDescription.id());
    }

    @Inject
    public void setErrorListInteractor(FakeErrorListInteractor errorListInteractor) {
        this.errorListInteractor = errorListInteractor;
    }

    @Inject
    public void setErrorRetryInteractor(ErrorRetryInteractor errorRetryInteractor) {
        this.errorRetryInteractor = errorRetryInteractor;
    }

    @Inject
    public void setErrorListNavigator(ErrorListNavigator errorListNavigator) {
        this.errorListNavigator = errorListNavigator;
    }
}
