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
import io.reactivex.rxjava3.core.Maybe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

class FoodItemAddInteractorImpl implements FoodItemAddInteractor {

    private static final Logger LOG = LoggerFactory.getLogger(FoodItemAddInteractorImpl.class);

    private final FoodItemAddService service;

    private final FoodItemAddRepository repository;

    private final Scheduler scheduler;

    private final Synchroniser synchroniser;

    private final ErrorRecorder errorRecorder;

    private final Localiser localiser;

    private final FoodItemFieldPredictor predictor;

    @Inject
    FoodItemAddInteractorImpl(FoodItemAddService service, FoodItemAddRepository repository, Scheduler scheduler, Synchroniser synchroniser, ErrorRecorder errorRecorder, Localiser localiser, Clock clock, FoodItemFieldPredictor predictor) {
        this.service = service;
        this.repository = repository;
        this.scheduler = scheduler;
        this.synchroniser = synchroniser;
        this.errorRecorder = errorRecorder;
        this.localiser = localiser;
        this.predictor = predictor;
    }

    @Override
    public Maybe<FoodItemAddData> getFormData(Id<Food> id) {
        Maybe<FoodForItemCreation> foodObservable = repository.getFood(id);
        var prediction = predictor.predictFor(foodObservable);
        Maybe<List<LocationForSelection>> locations = repository.getLocations();
        Maybe<List<ScaledUnitForSelection>> units = repository.getUnits();
        return Maybe.zip(foodObservable, locations, units, prediction.predictEatByDateAsync(), prediction.predictLocationAsync(), this::combine);
    }

    private FoodItemAddData combine(FoodForItemCreation f, List<LocationForSelection> l, List<ScaledUnitForSelection> u, LocalDate eatBy, Id<Location> location) {
        ListWithSuggestion<LocationForSelection> locationWithSuggestion = ListSearcher.searchFirstSuggested(l, location);
        ListWithSuggestion<ScaledUnitForSelection> unitWithSuggestion = ListSearcher.findFirstSuggestion(u, f.unit());
        return FoodItemAddData.create(f.toSelection(), eatBy, locationWithSuggestion, unitWithSuggestion);
    }

    @Override
    public void add(FoodItemForm item) {
        scheduler.schedule(Job.create(Job.Type.ADD_FOOD_ITEM, () -> addInBackground(item)));
    }

    void addInBackground(FoodItemForm item) {
        try {
            service.add(item.toNetwork(localiser));
            synchroniser.synchronise();
        } catch (SubsystemException e) {
            LOG.warn("failed to add food " + item);
            errorRecorder.recordFoodItemAddError(e, item.toErrorRecording(localiser));
            synchroniser.synchroniseAfterError(e);
        }
    }
}
