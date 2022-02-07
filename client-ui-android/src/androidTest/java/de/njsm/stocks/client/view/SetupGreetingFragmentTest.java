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
 */

package de.njsm.stocks.client.view;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.navigation.SetupGreetingNavigator;
import de.njsm.stocks.client.ui.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class SetupGreetingFragmentTest {

    private FragmentScenario<SetupGreetingFragment> scenario;

    private SetupGreetingNavigator setupGreetingNavigator;

    @Before
    public void setup() {
        scenario = FragmentScenario.launchInContainer(SetupGreetingFragment.class, new Bundle(), R.style.StocksTheme);
        scenario.onFragment(fragment -> ((Application) fragment.requireActivity().getApplication()).getDaggerRoot().inject(this));
    }

    @Test
    public void uiIsVisible() {
        onView(withId(R.id.fragment_setup_greeting_manual))
                .check(matches(isDisplayed()));
        onView(withText(R.string.text_qr_explanation))
                .check(matches(isDisplayed()));
        onView(withId(R.id.fragment_setup_greeting_scan))
                .check(matches(isDisplayed()));
    }

    @Test
    public void choosingManualSetupNavigates() {
        onView(withId(R.id.fragment_setup_greeting_manual)).perform(click());

        verify(setupGreetingNavigator).registerManually();
    }

    @Inject
    public void setSetupGreetingNavigator(SetupGreetingNavigator setupGreetingNavigator) {
        this.setupGreetingNavigator = setupGreetingNavigator;
    }
}
