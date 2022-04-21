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

import com.google.common.base.Preconditions;
import de.njsm.stocks.client.business.entities.Job;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SynchronisationLock implements Job.TypeVisitor<Boolean, Boolean> {

    private final ReadWriteLock lock;

    private final AtomicInteger counter;

    public SynchronisationLock() {
        lock = new ReentrantReadWriteLock();
        counter = new AtomicInteger();
    }

    @Override
    public Boolean setup(Job.Type type, Boolean aquireLock) {
        return true;
    }

    @Override
    public Boolean database(Job.Type type, Boolean aquireLock) {
        return true;
    }

    @Override
    public Boolean synchronisation(Job.Type type, Boolean aquireLock) {
        if (aquireLock) {
            if (!counter.compareAndSet(0, 1))
                return false;
        } else {
            Preconditions.checkState(counter.compareAndSet(1, 0), "counter has not been aquired first");
        }
        writeLock(aquireLock);
        return true;
    }

    @Override
    public Boolean addLocation(Job.Type type, Boolean aquireLock) {
        readLock(aquireLock);
        return true;
    }

    @Override
    public Boolean deleteError(Job.Type type, Boolean aquireLock) {
        return true;
    }

    @Override
    public Boolean deleteLocation(Job.Type type, Boolean aquireLock) {
        readLock(aquireLock);
        return true;
    }

    @Override
    public Boolean editLocation(Job.Type type, Boolean aquireLock) {
        readLock(aquireLock);
        return true;
    }

    @Override
    public Boolean unknown(Job.Type type, Boolean aquireLock) {
        return true;
    }

    private void readLock(boolean aquireLock) {
        if (aquireLock)
            lock.readLock().lock();
        else
            lock.readLock().unlock();
    }

    private void writeLock(boolean aquireLock) {
        if (aquireLock)
            lock.writeLock().lock();
        else
            lock.writeLock().unlock();
    }
}
