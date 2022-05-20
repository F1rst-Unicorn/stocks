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
import de.njsm.stocks.client.business.entities.ScaledUnitAddForm;
import de.njsm.stocks.client.business.entities.UnitForSelection;
import de.njsm.stocks.client.execution.Scheduler;
import io.reactivex.rxjava3.core.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

class ScaledUnitAddInteractorImpl implements ScaledUnitAddInteractor {

    private static final Logger LOG = LoggerFactory.getLogger(ScaledUnitAddInteractorImpl.class);

    private final ScaledUnitAddService scaledUnitAddService;

    private final ScaledUnitAddRepository scaledUnitAddRepository;

    private final Synchroniser synchroniser;

    private final ErrorRecorder errorRecorder;

    private final Scheduler scheduler;

    @Inject
    ScaledUnitAddInteractorImpl(ScaledUnitAddService scaledUnitAddService, ScaledUnitAddRepository scaledUnitAddRepository, Synchroniser synchroniser, ErrorRecorder errorRecorder, Scheduler scheduler) {
        this.scaledUnitAddService = scaledUnitAddService;
        this.scaledUnitAddRepository = scaledUnitAddRepository;
        this.synchroniser = synchroniser;
        this.errorRecorder = errorRecorder;
        this.scheduler = scheduler;
    }

    @Override
    public Observable<List<UnitForSelection>> getUnits() {
        return scaledUnitAddRepository.getUnitsForSelection();
    }

    @Override
    public void add(ScaledUnitAddForm form) {
        scheduler.schedule(Job.create(Job.Type.ADD_SCALED_UNIT, () -> addUnitInBackground(form)));
    }

    void addUnitInBackground(ScaledUnitAddForm form) {
        try {
            scaledUnitAddService.add(form);
            synchroniser.synchronise();
        } catch (SubsystemException e) {
            LOG.warn("failed to add unit " + form);
            errorRecorder.recordScaledUnitAddError(e, form);
        }
    }
}
