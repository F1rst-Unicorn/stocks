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
import de.njsm.stocks.client.business.entities.User;
import de.njsm.stocks.client.business.entities.Versionable;
import de.njsm.stocks.client.execution.Scheduler;

import javax.inject.Inject;

class UserDeleterImpl extends AbstractDeleterImpl<User> {

    @Inject
    UserDeleterImpl(
            EntityDeleteService<User> deleteService,
            EntityDeleteRepository<User> deleteRepository,
            Synchroniser synchroniser,
            ErrorRecorder errorRecorder,
            Scheduler scheduler) {
        super(deleteService, deleteRepository, synchroniser, errorRecorder, scheduler);
    }

    @Override
    Job.Type getJobType() {
        return Job.Type.DELETE_USER;
    }

    @Override
    void recordError(SubsystemException e, Versionable<User> data) {
        errorRecorder.recordUserDeleteError(e, data);
    }
}
