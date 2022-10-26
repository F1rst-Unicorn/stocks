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

import javax.inject.Inject;

class ErrorRetryInteractorImpl implements ErrorRetryInteractor, ErrorDetailsVisitor<Void, Void> {

    private final LocationAddInteractor locationAddInteractor;

    private final EntityDeleter<Location> locationDeleter;

    private final LocationEditInteractor locationEditInteractor;

    private final UnitAddInteractor unitAddInteractor;

    private final EntityDeleter<Unit> unitDeleter;

    private final UnitEditInteractor unitEditInteractor;

    private final ScaledUnitAddInteractor scaledUnitAddInteractor;

    private final ScaledUnitEditInteractor scaledUnitEditInteractor;

    private final EntityDeleter<ScaledUnit> scaledUnitDeleter;

    private final FoodAddInteractor foodAddInteractor;

    private final EntityDeleter<Food> foodDeleter;

    private final FoodEditInteractor foodEditInteractor;

    private final FoodItemAddInteractor foodItemAddInteractor;

    private final EntityDeleter<FoodItem> foodItemDeleter;

    private final Synchroniser synchroniser;

    private final Scheduler scheduler;

    private final ErrorRepository errorRepository;

    private final JobTypeTranslator jobTypeTranslator;

    @Inject
    ErrorRetryInteractorImpl(LocationAddInteractor locationAddInteractor,
                             EntityDeleter<Location> locationDeleter,
                             LocationEditInteractor locationEditInteractor,
                             UnitAddInteractor unitAddInteractor,
                             EntityDeleter<Unit> unitDeleter,
                             UnitEditInteractor unitEditInteractor,
                             ScaledUnitAddInteractor scaledUnitAddInteractor,
                             ScaledUnitEditInteractor scaledUnitEditInteractor,
                             EntityDeleter<ScaledUnit> scaledUnitDeleter,
                             FoodAddInteractor foodAddInteractor,
                             EntityDeleter<Food> foodDeleter,
                             FoodEditInteractor foodEditInteractor,
                             FoodItemAddInteractor foodItemAddInteractor,
                             EntityDeleter<FoodItem> foodItemDeleter,
                             Synchroniser synchroniser,
                             Scheduler scheduler,
                             ErrorRepository errorRepository) {
        this.locationAddInteractor = locationAddInteractor;
        this.locationDeleter = locationDeleter;
        this.locationEditInteractor = locationEditInteractor;
        this.unitAddInteractor = unitAddInteractor;
        this.unitDeleter = unitDeleter;
        this.unitEditInteractor = unitEditInteractor;
        this.scaledUnitAddInteractor = scaledUnitAddInteractor;
        this.scaledUnitEditInteractor = scaledUnitEditInteractor;
        this.scaledUnitDeleter = scaledUnitDeleter;
        this.foodAddInteractor = foodAddInteractor;
        this.foodDeleter = foodDeleter;
        this.foodEditInteractor = foodEditInteractor;
        this.foodItemAddInteractor = foodItemAddInteractor;
        this.foodItemDeleter = foodItemDeleter;
        this.synchroniser = synchroniser;
        this.scheduler = scheduler;
        this.errorRepository = errorRepository;
        this.jobTypeTranslator = new JobTypeTranslator();
    }

    @Override
    public void retry(ErrorDescription errorDescription) {
        scheduler.schedule(Job.create(jobTypeTranslator.visit(errorDescription.errorDetails(), null),
                () -> retryInBackground(errorDescription)));
    }

    @Override
    public void delete(ErrorDescription errorDescription) {
        scheduler.schedule(Job.create(Job.Type.DELETE_ERROR, () -> deleteInBackground(errorDescription)));
    }

    void retryInBackground(ErrorDescription errorDescription) {
        visit(errorDescription.errorDetails(), null);
        errorRepository.deleteError(errorDescription);
    }

    void deleteInBackground(ErrorDescription errorDescription) {
        errorRepository.deleteError(errorDescription);
    }

    @Override
    public Void locationAddForm(LocationAddForm locationAddForm, Void input) {
        locationAddInteractor.addLocation(locationAddForm);
        return null;
    }

    @Override
    public Void synchronisationErrorDetails(SynchronisationErrorDetails synchronisationErrorDetails, Void input) {
        synchroniser.synchronise();
        return null;
    }

    @Override
    public Void locationDeleteErrorDetails(LocationDeleteErrorDetails locationDeleteErrorDetails, Void input) {
        locationDeleter.delete(locationDeleteErrorDetails);
        return null;
    }

    @Override
    public Void locationEditErrorDetails(LocationEditErrorDetails locationEditErrorDetails, Void input) {
        LocationToEdit data = LocationToEdit.builder()
                .id(locationEditErrorDetails.id())
                .name(locationEditErrorDetails.name())
                .description(locationEditErrorDetails.description())
                .build();
        locationEditInteractor.edit(data);
        return null;
    }

    @Override
    public Void unitAddForm(UnitAddForm unitAddForm, Void input) {
        unitAddInteractor.addUnit(unitAddForm);
        return null;
    }

    @Override
    public Void unitDeleteErrorDetails(UnitDeleteErrorDetails unitDeleteErrorDetails, Void input) {
        unitDeleter.delete(unitDeleteErrorDetails);
        return null;
    }

    @Override
    public Void unitEditErrorDetails(UnitEditErrorDetails unitEditErrorDetails, Void input) {
        UnitToEdit data = UnitToEdit.builder()
                .id(unitEditErrorDetails.id())
                .name(unitEditErrorDetails.name())
                .abbreviation(unitEditErrorDetails.abbreviation())
                .build();
        unitEditInteractor.edit(data);
        return null;
    }

    @Override
    public Void scaledUnitAddErrorDetails(ScaledUnitAddErrorDetails scaledUnitAddErrorDetails, Void input) {
        ScaledUnitAddForm data = ScaledUnitAddForm.create(
                scaledUnitAddErrorDetails.scale(),
                scaledUnitAddErrorDetails.unit()
        );
        scaledUnitAddInteractor.add(data);
        return null;
    }

    @Override
    public Void scaledUnitEditErrorDetails(ScaledUnitEditErrorDetails scaledUnitEditErrorDetails, Void input) {
        ScaledUnitToEdit data = ScaledUnitToEdit.create(
                scaledUnitEditErrorDetails.id(),
                scaledUnitEditErrorDetails.scale(),
                scaledUnitEditErrorDetails.unit());

        scaledUnitEditInteractor.edit(data);
        return null;
    }

    @Override
    public Void scaledUnitDeleteErrorDetails(ScaledUnitDeleteErrorDetails scaledUnitDeleteErrorDetails, Void input) {
        scaledUnitDeleter.delete(scaledUnitDeleteErrorDetails);
        return null;
    }

    @Override
    public Void foodAddErrorDetails(FoodAddErrorDetails foodAddErrorDetails, Void input) {
        FoodAddForm data = FoodAddForm.create(foodAddErrorDetails.name(),
                foodAddErrorDetails.toBuy(),
                foodAddErrorDetails.expirationOffset(),
                foodAddErrorDetails.location().orElse(null),
                foodAddErrorDetails.storeUnit(),
                foodAddErrorDetails.description());

        foodAddInteractor.add(data);
        return null;
    }

    @Override
    public Void foodEditErrorDetails(FoodEditErrorDetails foodEditErrorDetails, Void input) {
        FoodToEdit data = FoodToEdit.create(foodEditErrorDetails.id(),
                foodEditErrorDetails.name(),
                foodEditErrorDetails.expirationOffset(),
                foodEditErrorDetails.location(),
                foodEditErrorDetails.storeUnit(),
                foodEditErrorDetails.description());

        foodEditInteractor.edit(data);
        return null;
    }

    @Override
    public Void foodItemAddErrorDetails(FoodItemAddErrorDetails foodItemAddErrorDetails, Void input) {
        foodItemAddInteractor.add(foodItemAddErrorDetails.into());
        return null;
    }

    @Override
    public Void foodDeleteErrorDetails(FoodDeleteErrorDetails foodDeleteErrorDetails, Void input) {
        foodDeleter.delete(foodDeleteErrorDetails);
        return null;
    }

    @Override
    public Void foodItemDeleteErrorDetails(FoodItemDeleteErrorDetails foodItemDeleteErrorDetails, Void input) {
        foodItemDeleter.delete(foodItemDeleteErrorDetails);
        return null;
    }

    private static final class JobTypeTranslator implements ErrorDetailsVisitor<Void, Job.Type> {

        @Override
        public Job.Type locationAddForm(LocationAddForm locationAddForm, Void input) {
            return Job.Type.ADD_LOCATION;
        }

        @Override
        public Job.Type synchronisationErrorDetails(SynchronisationErrorDetails synchronisationErrorDetails, Void input) {
            return Job.Type.SYNCHRONISATION;
        }

        @Override
        public Job.Type locationDeleteErrorDetails(LocationDeleteErrorDetails locationDeleteErrorDetails, Void input) {
            return Job.Type.DELETE_LOCATION;
        }

        @Override
        public Job.Type locationEditErrorDetails(LocationEditErrorDetails locationEditErrorDetails, Void input) {
            return Job.Type.EDIT_LOCATION;
        }

        @Override
        public Job.Type unitAddForm(UnitAddForm unitAddForm, Void input) {
            return Job.Type.ADD_UNIT;
        }

        @Override
        public Job.Type unitDeleteErrorDetails(UnitDeleteErrorDetails unitDeleteErrorDetails, Void input) {
            return Job.Type.DELETE_UNIT;
        }

        @Override
        public Job.Type unitEditErrorDetails(UnitEditErrorDetails unitEditErrorDetails, Void input) {
            return Job.Type.EDIT_UNIT;
        }

        @Override
        public Job.Type scaledUnitAddErrorDetails(ScaledUnitAddErrorDetails scaledUnitAddErrorDetails, Void input) {
            return Job.Type.ADD_SCALED_UNIT;
        }

        @Override
        public Job.Type scaledUnitEditErrorDetails(ScaledUnitEditErrorDetails scaledUnitEditErrorDetails, Void input) {
            return Job.Type.EDIT_SCALED_UNIT;
        }

        @Override
        public Job.Type scaledUnitDeleteErrorDetails(ScaledUnitDeleteErrorDetails scaledUnitDeleteErrorDetails, Void input) {
            return Job.Type.DELETE_SCALED_UNIT;
        }

        @Override
        public Job.Type foodAddErrorDetails(FoodAddErrorDetails foodAddErrorDetails, Void input) {
            return Job.Type.ADD_FOOD;
        }

        @Override
        public Job.Type foodDeleteErrorDetails(FoodDeleteErrorDetails foodDeleteErrorDetails, Void input) {
            return Job.Type.DELETE_FOOD;
        }

        @Override
        public Job.Type foodEditErrorDetails(FoodEditErrorDetails foodEditErrorDetails, Void input) {
            return Job.Type.EDIT_FOOD;
        }

        @Override
        public Job.Type foodItemAddErrorDetails(FoodItemAddErrorDetails foodItemAddErrorDetails, Void input) {
            return Job.Type.ADD_FOOD_ITEM;
        }

        @Override
        public Job.Type foodItemDeleteErrorDetails(FoodItemDeleteErrorDetails foodItemDeleteErrorDetails, Void input) {
            return Job.Type.DELETE_FOOD_ITEM;
        }
    }
}
