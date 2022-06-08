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

package de.njsm.stocks.client.business;

import de.njsm.stocks.client.business.entities.Job;
import de.njsm.stocks.client.execution.Scheduler;

import javax.inject.Inject;

class SynchroniserImpl implements Synchroniser {

    private final SynchroniseInteractor synchroniseInteractor;

    private final Scheduler scheduler;

    @Inject
    SynchroniserImpl(SynchroniseInteractor synchroniseInteractor, Scheduler scheduler) {
        this.synchroniseInteractor = synchroniseInteractor;
        this.scheduler = scheduler;
    }

    @Override
    public void synchronise() {
        scheduler.schedule(Job.create(Job.Type.SYNCHRONISATION, synchroniseInteractor::synchronise));
    }

    @Override
    public void synchroniseAfterError(SubsystemException e) {
        new AfterErrorSynchroniser().visit(e, null);
    }

    private final class AfterErrorSynchroniser implements SubsystemException.Visitor<Void, Void> {

        @Override
        public Void statusCodeException(StatusCodeException exception, Void input) {
            if (exception.getStatusCode().isTriggeredByOutdatedLocalData()) {
                synchronise();
            }
            return null;
        }

        @Override
        public Void subsystemException(SubsystemException exception, Void input) {
            return null;
        }
    }
}
