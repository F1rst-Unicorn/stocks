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

package de.njsm.stocks.client.fragment.settings;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.SettingsInteractor;
import de.njsm.stocks.client.business.entities.Settings;
import de.njsm.stocks.client.navigation.SettingsNavigator;
import de.njsm.stocks.client.ui.R;
import io.reactivex.rxjava3.core.Observable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.function.BiConsumer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static de.njsm.stocks.client.Matchers.recyclerView;
import static org.mockito.Mockito.*;

public class SettingsFragmentTest {

    private FragmentScenario<SettingsFragment> scenario;

    private SettingsInteractor interactor;

    private SettingsNavigator navigator;

    @Before
    public void setUp() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        when(interactor.getData()).thenReturn(Observable.just(getInput()));
        scenario = FragmentScenario.launchInContainer(SettingsFragment.class, new Bundle(), R.style.StocksTheme);
    }

    @After
    public void tearDown() {
        reset(interactor);
        reset(navigator);
    }

    @Test
    public void initialSettingsAreShown() {
        onView(recyclerView(androidx.preference.R.id.recycler_view)
                .atPositionOnView(1, android.R.id.summary))
                .check(matches(withText(getInput().serverName())));
        onView(recyclerView(androidx.preference.R.id.recycler_view)
                .atPositionOnView(2, android.R.id.summary))
                .check(matches(withText(String.valueOf(getInput().caPort()))));
        onView(recyclerView(androidx.preference.R.id.recycler_view)
                .atPositionOnView(3, android.R.id.summary))
                .check(matches(withText(String.valueOf(getInput().registrationPort()))));
        onView(recyclerView(androidx.preference.R.id.recycler_view)
                .atPositionOnView(4, android.R.id.summary))
                .check(matches(withText(String.valueOf(getInput().serverPort()))));
    }

    @Test
    public void changingServerNamePropagates() {
        String input = "test";

        onView(recyclerView(androidx.preference.R.id.recycler_view).atPosition(1)).perform(click());
        onView(withId(android.R.id.edit)).perform(replaceText(input));
        onView(withId(android.R.id.button1)).perform(click());

        verify(interactor).updateServerName(input);
    }

    @Test
    public void changingCaPortPropagates() {
        changingPortPropagates(2, SettingsInteractor::updateCaPort);
    }

    @Test
    public void changingRegistrationPortPropagates() {
        changingPortPropagates(3, SettingsInteractor::updateRegistrationPort);
    }

    @Test
    public void changingServerPortPropagates() {
        changingPortPropagates(4, SettingsInteractor::updateServerPort);
    }

    private void changingPortPropagates(int position, BiConsumer<SettingsInteractor, Integer> callback) {
        int input = 123;

        onView(recyclerView(androidx.preference.R.id.recycler_view).atPosition(position)).perform(click());
        onView(withId(android.R.id.edit)).perform(replaceText(String.valueOf(input)));
        onView(withId(android.R.id.button1)).perform(click());

        callback.accept(verify(interactor), input);
    }

    @Test
    public void invalidCaPortIsNotSaved() {
        invalidPortIsNotSaved(2, SettingsInteractor::updateCaPort);
    }

    @Test
    public void invalidRegistrationPortIsNotSaved() {
        invalidPortIsNotSaved(3, SettingsInteractor::updateRegistrationPort);
    }

    @Test
    public void invalidServerPortIsNotSaved() {
        invalidPortIsNotSaved(4, SettingsInteractor::updateServerPort);
    }

    private void invalidPortIsNotSaved(int position, BiConsumer<SettingsInteractor, Integer> callback) {
        String input = "no number";

        onView(recyclerView(androidx.preference.R.id.recycler_view).atPosition(position)).perform(click());
        onView(withId(android.R.id.edit)).perform(replaceText(input));
        onView(withId(android.R.id.button1)).perform(click());

        callback.accept(verify(interactor, never()), anyInt());
    }

    @Test
    public void fullSyncCallsInteractor() {
        onView(recyclerView(androidx.preference.R.id.recycler_view).atPosition(6)).perform(click());

        verify(interactor).performFullSync();
    }

    @Test
    public void goingToCrashLogsNavigates() {
        onView(recyclerView(androidx.preference.R.id.recycler_view).atPosition(7)).perform(click());

        verify(navigator).showCrashLogs();
    }

    @Test
    public void deletingSearchHistoryWorks() {
        onView(recyclerView(androidx.preference.R.id.recycler_view).atPosition(8)).perform(click());

        verify(interactor).clearSearchHistory();
    }

    private Settings getInput() {
        return Settings.create("test.example", 123, 124, 125);
    }

    @Inject
    void setInteractor(SettingsInteractor interactor) {
        this.interactor = interactor;
    }

    @Inject
    void setNavigator(SettingsNavigator navigator) {
        this.navigator = navigator;
    }
}
