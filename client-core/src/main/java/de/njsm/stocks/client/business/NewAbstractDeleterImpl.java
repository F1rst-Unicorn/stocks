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

abstract class NewAbstractDeleterImpl<E> implements EntityDeleteInteractor<E> {

    final NewEntityDeleteService<E> deleteService;

    final Synchroniser synchroniser;

    final ErrorRecorder errorRecorder;

    final Scheduler scheduler;

    NewAbstractDeleterImpl(NewEntityDeleteService<E> deleteService, Synchroniser synchroniser, ErrorRecorder errorRecorder, Scheduler scheduler) {
        this.deleteService = deleteService;
        this.synchroniser = synchroniser;
        this.errorRecorder = errorRecorder;
        this.scheduler = scheduler;
    }

    @Override
    public void delete(E entity) {
        scheduler.schedule(Job.create(getJobType(), () -> deleteInBackground(entity)));
    }

    void deleteInBackground(E entity) {
        try {
            deleteService.delete(entity);
            synchroniser.synchronise();
        } catch (SubsystemException e) {
            recordError(e, entity);
            synchroniser.synchroniseAfterError(e);
        }
    }

    abstract Job.Type getJobType();

    abstract void recordError(SubsystemException e, E data);
}
