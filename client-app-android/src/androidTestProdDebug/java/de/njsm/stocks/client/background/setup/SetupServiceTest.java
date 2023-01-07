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

package de.njsm.stocks.client.background.setup;

import android.content.Intent;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.rule.ServiceTestRule;
import de.njsm.stocks.client.TestApplication;
import de.njsm.stocks.client.business.SetupRunner;
import de.njsm.stocks.client.business.entities.Job;
import de.njsm.stocks.client.execution.Scheduler;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

public class SetupServiceTest {

    @Rule
    public final ServiceTestRule serviceTestRule = ServiceTestRule.withTimeout(5L, TimeUnit.SECONDS);

    private Scheduler scheduler;

    private SetupRunner setupRunner;

    @Before
    public void setUp() {
        ((TestApplication) ApplicationProvider.getApplicationContext()).getDaggerRoot().inject(this);
    }

    @After
    public void tearDown() {
        reset(scheduler);
        reset(setupRunner);
    }

    @Test
    public void test() throws TimeoutException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SetupService.class);

        serviceTestRule.startService(intent);

        ArgumentCaptor<Job> captor = ArgumentCaptor.forClass(Job.class);
        verify(scheduler).schedule(captor.capture());
        captor.getValue().runnable().run();
        verify(setupRunner).setup();
    }

    @Inject
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Inject
    public void setSetupRunner(SetupRunner setupRunner) {
        this.setupRunner = setupRunner;
    }
}
