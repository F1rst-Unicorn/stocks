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
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

class SchedulerImpl implements Scheduler, SchedulerStatusReporter {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerImpl.class);

    private final Executor executor;

    private final AtomicInteger counter;

    private final Subject<Integer> numberOfRunningJobs;

    private final SynchronisationLock lock;

    @Inject
    SchedulerImpl(Executor executor, SynchronisationLock lock) {
        this.executor = executor;
        counter = new AtomicInteger(0);
        numberOfRunningJobs = BehaviorSubject.createDefault(counter.get()).toSerialized();
        this.lock = lock;
    }

    @Override
    public void schedule(Job job) {
        if (job.name() != Job.Type.DATABASE) {
            LOG.trace(job + " scheduled");
        }
        numberOfRunningJobs.onNext(counter.incrementAndGet());

        executor.execute(() -> {
            if (!lock.visit(job.name(), true)) {
                LOG.trace(job + " skipped");
                numberOfRunningJobs.onNext(counter.decrementAndGet());
                return;
            }
            if (job.name() != Job.Type.DATABASE) {
                LOG.trace(job + " started");
            }
            job.runnable().run();
            numberOfRunningJobs.onNext(counter.decrementAndGet());
            if (job.name() != Job.Type.DATABASE) {
                LOG.trace(job + " stopped");
            }
            lock.visit(job.name(), false);
        });
    }

    @Override
    public io.reactivex.rxjava3.core.Scheduler into() {
        return Schedulers.from(executor);
    }

    @Override
    public Observable<Integer> getNumberOfRunningJobs() {
        return numberOfRunningJobs;
    }
}
