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

package de.njsm.stocks.client.execution;


import de.njsm.stocks.client.business.entities.Job;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SchedulerImplTest {

    private SchedulerImpl uut;

    @Mock
    private Executor executor;

    @Mock
    private SynchronisationLock lock;

    @BeforeEach
    public void setUp() {
        uut = new SchedulerImpl(executor, lock);
    }

    @Test
    public void newSchedulerHasNoJobs() {
        uut.getNumberOfRunningJobs().test().assertValue(0);
    }

    @Test
    public void schedulingJobWorks() {
        Job input = Job.create(Job.Type.UNKNOWN, () -> {});
        uut.schedule(input);
        verify(executor).execute(any(Runnable.class));
    }

    @Test
    public void schedulingAJobIncreasesTheCounter() {
        Job input = Job.create(Job.Type.UNKNOWN, () -> {});
        uut.schedule(input);
        uut.getNumberOfRunningJobs().test().assertValue(1);
    }

    @Test
    void completingAJobDecreasesTheCounter() {
        when(lock.visit(any(), anyBoolean())).thenReturn(true);
        Job input = Job.create(Job.Type.UNKNOWN, mock(Runnable.class));

        uut.schedule(input);
        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(executor).execute(captor.capture());
        captor.getValue().run();

        uut.getNumberOfRunningJobs().test().assertValue(0);
        verify(input.runnable()).run();
    }

    public static void runJobOnMocked(Scheduler scheduler) {
        ArgumentCaptor<Job> captor = ArgumentCaptor.forClass(Job.class);
        verify(scheduler).schedule(captor.capture());
        assertEquals(1, captor.getAllValues().size(), "this method only supports one captured value");
        captor.getValue().runnable().run();
    }
}
