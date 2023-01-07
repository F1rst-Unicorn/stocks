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

package de.njsm.stocks.client.fragment;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.ErrorStatusReporter;
import de.njsm.stocks.client.execution.SchedulerStatusReporter;
import de.njsm.stocks.client.navigation.BottomToolbarNavigator;
import de.njsm.stocks.client.ui.R;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BottomToolbarFragmentTest {

    private FragmentScenario<BottomToolbarFragment> scenario;

    private SchedulerStatusReporter schedulerStatusReporter;

    private ErrorStatusReporter errorStatusReporter;

    private BehaviorSubject<Integer> backgroundJobCounter;

    private BehaviorSubject<Integer> errorCounter;

    private BottomToolbarNavigator navigator;

    @Before
    public void setup() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);

        backgroundJobCounter = BehaviorSubject.create();
        errorCounter = BehaviorSubject.create();
        when(schedulerStatusReporter.getNumberOfRunningJobs()).thenReturn(backgroundJobCounter);
        when(errorStatusReporter.getNumberOfErrors()).thenReturn(errorCounter);

        scenario = FragmentScenario.launchInContainer(BottomToolbarFragment.class, new Bundle(), R.style.StocksTheme);
    }

    @Test
    public void backgroundJobCounterIsInvisibleByDefault() {
        backgroundJobCounter.onNext(0);

        onView(withId(R.id.fragment_frame_toolbar_background_layout))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
    }

    @Test
    public void initialPresentBackgroundJobIsShown() {
        backgroundJobCounter.onNext(1);

        onView(withId(R.id.fragment_frame_toolbar_background_layout))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void changingCounterChangesVisibility() {
        backgroundJobCounter.onNext(0);
        onView(withId(R.id.fragment_frame_toolbar_background_layout))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));

        backgroundJobCounter.onNext(1);
        onView(withId(R.id.fragment_frame_toolbar_background_layout))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        backgroundJobCounter.onNext(0);
        onView(withId(R.id.fragment_frame_toolbar_background_layout))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
    }

    @Test
    public void errorIndicatorShows() {
        errorCounter.onNext(0);
        onView(withId(R.id.fragment_frame_toolbar_error_layout))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));

        errorCounter.onNext(1);
        onView(withId(R.id.fragment_frame_toolbar_error_layout))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        errorCounter.onNext(0);
        onView(withId(R.id.fragment_frame_toolbar_error_layout))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
    }

    @Test
    public void clickingErrorIndicatorNavigates() {
        errorCounter.onNext(1);

        onView(withId(R.id.fragment_frame_toolbar_error_layout))
                .perform(click());

        verify(navigator).goToErrors();
    }

    @Inject
    public void setSchedulerStatusReporter(SchedulerStatusReporter schedulerStatusReporter) {
        this.schedulerStatusReporter = schedulerStatusReporter;
    }

    @Inject
    public void setErrorStatusReporter(ErrorStatusReporter errorStatusReporter) {
        this.errorStatusReporter = errorStatusReporter;
    }

    @Inject
    public void setNavigator(BottomToolbarNavigator navigator) {
        this.navigator = navigator;
    }
}
