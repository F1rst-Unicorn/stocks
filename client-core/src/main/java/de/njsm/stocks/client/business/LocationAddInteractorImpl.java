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

import com.google.common.annotations.VisibleForTesting;
import de.njsm.stocks.client.business.entities.Job;
import de.njsm.stocks.client.business.entities.LocationAddForm;
import de.njsm.stocks.client.execution.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class LocationAddInteractorImpl implements LocationAddInteractor {

    private static final Logger LOG = LoggerFactory.getLogger(LocationAddInteractorImpl.class);

    private final Scheduler scheduler;

    private final ErrorRecorder errorRecorder;

    private final LocationAddService locationAddService;

    private final Synchroniser synchroniser;

    @Inject
    public LocationAddInteractorImpl(Scheduler scheduler, ErrorRecorder errorRecorder, LocationAddService locationAddService, Synchroniser synchroniser) {
        this.scheduler = scheduler;
        this.errorRecorder = errorRecorder;
        this.locationAddService = locationAddService;
        this.synchroniser = synchroniser;
    }

    @Override
    public void addLocation(LocationAddForm locationAddForm) {
        scheduler.schedule(Job.create(Job.Type.ADD_LOCATION, () -> addLocationInBackground(locationAddForm)));
    }

    @VisibleForTesting
    void addLocationInBackground(LocationAddForm locationAddForm) {
        try {
            locationAddService.add(locationAddForm);
            synchroniser.synchronise();
        } catch (SubsystemException e) {
            LOG.warn("failed to add location " + locationAddForm);
            errorRecorder.recordLocationAddError(e, locationAddForm);
        }
    }
}
