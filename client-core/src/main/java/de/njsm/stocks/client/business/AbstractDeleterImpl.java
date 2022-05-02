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

import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.execution.Scheduler;

abstract class AbstractDeleterImpl<E extends Entity<E>> implements EntityDeleter<E> {

    final EntityDeleteService<E> deleteService;

    final EntityDeleteRepository<E> entityDeleteRepository;

    final Synchroniser synchroniser;

    final ErrorRecorder errorRecorder;

    final Scheduler scheduler;

    AbstractDeleterImpl(EntityDeleteService<E> deleteService, EntityDeleteRepository<E> entityDeleteRepository, Synchroniser synchroniser, ErrorRecorder errorRecorder, Scheduler scheduler) {
        this.deleteService = deleteService;
        this.entityDeleteRepository = entityDeleteRepository;
        this.synchroniser = synchroniser;
        this.errorRecorder = errorRecorder;
        this.scheduler = scheduler;
    }

    @Override
    public void delete(Identifiable<E> id) {
        scheduler.schedule(Job.create(getJobType(), () -> deleteLocationInBackground(id)));
    }

    void deleteLocationInBackground(Identifiable<E> id) {
        Versionable<E> data = entityDeleteRepository.getEntityForDeletion(id);
        try {
            deleteService.delete(data);
            synchroniser.synchronise();
        } catch (SubsystemException e) {
            recordError(e, data);
            new AfterErrorSynchroniser(synchroniser).visit(e, null);
        }
    }

    abstract Job.Type getJobType();

    abstract void recordError(SubsystemException e, Versionable<E> data);
}
