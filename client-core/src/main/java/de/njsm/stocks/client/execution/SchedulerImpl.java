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
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;

import javax.inject.Inject;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

class SchedulerImpl implements Scheduler, SchedulerStatusReporter {

    private final Executor executor;

    private final AtomicInteger counter;

    private final Subject<Integer> numberOfRunningJobs;

    @Inject
    SchedulerImpl(Executor executor) {
        this.executor = executor;
        counter = new AtomicInteger(0);
        numberOfRunningJobs = BehaviorSubject.createDefault(counter.get()).toSerialized();
    }

    @Override
    public void schedule(Job job) {
        numberOfRunningJobs.onNext(counter.incrementAndGet());
        executor.execute(() -> {
            job.runnable().run();
            numberOfRunningJobs.onNext(counter.decrementAndGet());
        });
    }

    @Override
    public Observable<Integer> getNumberOfRunningJobs() {
        return numberOfRunningJobs;
    }
}
