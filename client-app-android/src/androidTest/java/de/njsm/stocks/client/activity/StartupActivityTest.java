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

package de.njsm.stocks.client.activity;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.TestApplication;
import de.njsm.stocks.client.business.SetupStatusChecker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasFlag;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

public class StartupActivityTest {

    private ActivityScenario<StartupActivity> scenario;

    private SetupStatusChecker setupStatusChecker;

    @Before
    public void setUp() {
        ((TestApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        Intents.init();
    }

    @After
    public void tearDown() {
        reset(setupStatusChecker);
        Intents.release();
    }

    @Test
    public void whenSetupIsNeededNavigateToSetup() {
        when(setupStatusChecker.isSetup()).thenReturn(false);

        scenario = ActivityScenario.launch(StartupActivity.class);

        intended(allOf(
                hasComponent(hasClassName(SetupActivity.class.getName())),
                hasFlag(FLAG_ACTIVITY_CLEAR_TASK),
                hasFlag(FLAG_ACTIVITY_NEW_TASK)
        ));
    }

    @Test
    public void whenSetupIsFinishedNavigateToMainMenu() {
        when(setupStatusChecker.isSetup()).thenReturn(true);

        scenario = ActivityScenario.launch(StartupActivity.class);

        intended(allOf(
                hasComponent(hasClassName(MainActivity.class.getName())),
                hasFlag(FLAG_ACTIVITY_CLEAR_TASK),
                hasFlag(FLAG_ACTIVITY_NEW_TASK)
        ));
    }

    @Inject
    void setSetupStatusChecker(SetupStatusChecker setupStatusChecker) {
        this.setupStatusChecker = setupStatusChecker;
    }
}
