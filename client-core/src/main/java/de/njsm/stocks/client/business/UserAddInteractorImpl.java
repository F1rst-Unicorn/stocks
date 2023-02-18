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
import de.njsm.stocks.client.business.entities.UserAddForm;
import de.njsm.stocks.client.execution.Scheduler;

import javax.inject.Inject;

class UserAddInteractorImpl implements UserAddInteractor {

    private final UserAddService service;

    private final Synchroniser synchroniser;

    private final Scheduler scheduler;

    private final ErrorRecorder errorRecorder;

    @Inject
    UserAddInteractorImpl(UserAddService service, Synchroniser synchroniser, Scheduler scheduler, ErrorRecorder errorRecorder) {
        this.service = service;
        this.synchroniser = synchroniser;
        this.scheduler = scheduler;
        this.errorRecorder = errorRecorder;
    }

    @Override
    public void add(UserAddForm form) {
        scheduler.schedule(Job.create(Job.Type.ADD_USER, () -> addInternally(form)));
    }

    private void addInternally(UserAddForm form) {
        try {
            service.add(form);
            synchroniser.synchronise();
        } catch (SubsystemException e) {
            errorRecorder.recordUserAddError(e, form);
            synchroniser.synchroniseAfterError(e);
        }
    }
}
