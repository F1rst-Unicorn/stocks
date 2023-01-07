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
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.execution.Scheduler;
import io.reactivex.rxjava3.core.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

class FoodAddInteractorImpl implements FoodAddInteractor {

    private static final Logger LOG = LoggerFactory.getLogger(FoodAddInteractorImpl.class);

    private final FoodAddService foodAddService;

    private final FoodAddRepository foodAddRepository;

    private final ErrorRecorder errorRecorder;

    private final Scheduler scheduler;

    private final Synchroniser synchroniser;

    @Inject
    FoodAddInteractorImpl(FoodAddService foodAddService, FoodAddRepository foodAddRepository, ErrorRecorder errorRecorder, Scheduler scheduler, Synchroniser synchroniser) {
        this.foodAddService = foodAddService;
        this.foodAddRepository = foodAddRepository;
        this.errorRecorder = errorRecorder;
        this.scheduler = scheduler;
        this.synchroniser = synchroniser;
    }

    @Override
    public void add(FoodAddForm form) {
        scheduler.schedule(Job.create(Job.Type.ADD_FOOD, () -> addInBackground(form)));
    }

    @Override
    public Observable<List<ScaledUnitForSelection>> getUnits() {
        return foodAddRepository.getUnits();
    }

    @Override
    public Observable<List<LocationForSelection>> getLocations() {
        return foodAddRepository.getLocations();
    }

    @VisibleForTesting
    void addInBackground(FoodAddForm form) {
        try {
            foodAddService.add(form);
            synchroniser.synchronise();
        } catch (SubsystemException e) {
            LOG.warn("failed to add food " + form);
            errorRecorder.recordFoodAddError(e, form);
        }
    }
}
