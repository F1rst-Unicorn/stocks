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

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import de.njsm.stocks.client.runtime.ExceptionHandler;

import javax.inject.Singleton;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicInteger;

@Module
public interface ExecutionModule {

    @Binds
    Scheduler scheduler(SchedulerImpl impl);

    @Binds
    SchedulerStatusReporter schedulerStatusReporter(SchedulerImpl impl);

    @Provides
    @Singleton
    static SchedulerImpl schedulerImpl(Executor executor, SynchronisationLock lock) {
        return new SchedulerImpl(executor, lock);
    }

    @Provides
    static AtomicInteger atomicInteger() {
        return new AtomicInteger();
    }

    @Provides
    @Singleton
    static Executor executor(ExceptionHandler exceptionHandler) {
        return new ForkJoinPool(4, new ForkJoinPool.ForkJoinWorkerThreadFactory() {

            int numberOfThreads = 0;

            @Override
            public ForkJoinWorkerThread newThread(ForkJoinPool forkJoinPool) {
                ForkJoinWorkerThread result = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(forkJoinPool);
                result.setName("stocks-background-" + numberOfThreads++);
                return result;
            }
        }, exceptionHandler, true);
    }

}
