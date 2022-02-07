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

package de.njsm.stocks.client.execution;


import de.njsm.stocks.client.business.entities.Job;
import de.njsm.stocks.client.execution.SchedulerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.concurrent.Executor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

public class SchedulerImplTest {

    private SchedulerImpl uut;

    private Executor executor;

    @BeforeEach
    public void setUp() {
        executor = Mockito.mock(Executor.class);

        uut = new SchedulerImpl(executor);
    }

    @Test
    public void newSchedulerHasNoJobs() {
        uut.getNumberOfRunningJobs().test().assertValue(0);
    }

    @Test
    public void schedulingJobWorks() {
        Job input = Job.create(0, () -> {});
        uut.schedule(input);
        verify(executor).execute(any(Runnable.class));
    }

    @Test
    public void schedulingAJobIncreasesTheCounter() {
        Job input = Job.create(0, () -> {});
        uut.schedule(input);
        uut.getNumberOfRunningJobs().test().assertValue(1);
    }

    @Test
    void completingAJobDecreasesTheCounter() {
        Job input = Job.create(0, () -> {});

        uut.schedule(input);
        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(executor).execute(captor.capture());
        captor.getValue().run();

        uut.getNumberOfRunningJobs().test().assertValue(0);
    }
}
