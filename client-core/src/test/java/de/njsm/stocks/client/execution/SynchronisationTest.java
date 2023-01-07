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
import org.junit.jupiter.api.RepeatedTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class SynchronisationTest {

    private SchedulerImpl uut;

    private ForkJoinPool executor;

    private AtomicInteger counter;

    @BeforeEach
    public void setUp() {
        counter = new AtomicInteger(0);
        executor = new ForkJoinPool(3);
        uut = new SchedulerImpl(executor, new SynchronisationLock(counter));
    }

    @RepeatedTest(20)
    void synchronisationWaitsForWriteFirst() {
        Runnable sync = mock(Runnable.class);
        Runnable deletion = mock(Runnable.class);
        CountDownLatch startSign = new CountDownLatch(1);
        CountDownLatch stopSign = new CountDownLatch(1);
        Job deleter = Job.create(Job.Type.DELETE_LOCATION, () -> {
            try {
                startSign.await();
                deletion.run();
                stopSign.await();
                uut.schedule(Job.create(Job.Type.SYNCHRONISATION, sync));
            } catch (InterruptedException e) {
                fail("interrupted");
            }
        });
        uut.schedule(deleter);

        verifyNoInteractions(deletion);
        verifyNoInteractions(sync);

        startSign.countDown();

        verify(deletion, timeout(1000)).run();
        verifyNoInteractions(sync);

        stopSign.countDown();

        verify(sync, timeout(1000)).run();
    }

    @RepeatedTest(20)
    void synchronisationWaitsForAllWritesFirst() {
        Runnable sync = mock(Runnable.class);
        Runnable deletion = mock(Runnable.class);
        ReentrantReadWriteLock startSign = new ReentrantReadWriteLock();
        CountDownLatch stopSign = new CountDownLatch(1);
        startSign.writeLock().lock();
        Job deleter = Job.create(Job.Type.DELETE_LOCATION, () -> {
            try {
                startSign.readLock().lock();
                startSign.readLock().unlock();
                deletion.run();
                stopSign.await();
            } catch (InterruptedException e) {
                fail("interrupted");
            }
        });
        uut.schedule(deleter);
        uut.schedule(deleter);

        while (startSign.getQueueLength() < 2);
        uut.schedule(Job.create(Job.Type.SYNCHRONISATION, sync));

        verifyNoInteractions(deletion);
        verifyNoInteractions(sync);

        startSign.writeLock().unlock();

        verify(deletion, timeout(1000).times(2)).run();
        verifyNoInteractions(sync);

        stopSign.countDown();

        verify(sync, timeout(1000)).run();
    }

    @RepeatedTest(20)
    void twoScheduledSyncsLeadToOneActualExecution() throws InterruptedException {
        Runnable sync = mock(Runnable.class);
        Runnable deletion = mock(Runnable.class);
        CountDownLatch startSign = new CountDownLatch(1);
        CountDownLatch stopSign = new CountDownLatch(1);
        CountDownLatch syncReadySign = new CountDownLatch(1);
        Job deleter = Job.create(Job.Type.DELETE_LOCATION, () -> {
            try {
                startSign.await();
                deletion.run();
                stopSign.await();
                uut.schedule(Job.create(Job.Type.SYNCHRONISATION, sync));
                syncReadySign.await();
            } catch (InterruptedException e) {
                fail("interrupted");
            }
        });
        uut.schedule(deleter);
        uut.schedule(deleter);

        verifyNoInteractions(deletion);
        verifyNoInteractions(sync);

        startSign.countDown();

        verify(deletion, timeout(1000).times(2)).run();
        verifyNoInteractions(sync);

        stopSign.countDown();

        while (counter.get() == 0);
        syncReadySign.countDown();

        executor.shutdown();
        assertTrue(executor.awaitTermination(5, SECONDS));
        verify(sync, times(1)).run();
        verifyNoMoreInteractions(sync);
    }

    @RepeatedTest(20)
    void sequentialSynchronisationsRunEach() {
        Runnable sync = mock(Runnable.class);
        uut.schedule(Job.create(Job.Type.SYNCHRONISATION, sync));

        verify(sync, timeout(1000)).run();
        reset(sync);

        uut.schedule(Job.create(Job.Type.SYNCHRONISATION, sync));

        verify(sync, timeout(1000)).run();
    }
}
