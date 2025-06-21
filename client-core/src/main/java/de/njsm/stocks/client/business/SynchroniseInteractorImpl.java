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

import com.google.common.collect.Comparators;
import de.njsm.stocks.client.business.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

class SynchroniseInteractorImpl implements SynchroniseInteractor {

    private static final Logger LOG = LoggerFactory.getLogger(SynchroniseInteractorImpl.class);

    private final UpdateService updateService;

    private final SynchronisationRepository synchronisationRepository;

    private final ErrorRecorder errorRecorder;

    @Inject
    SynchroniseInteractorImpl(UpdateService updateService, SynchronisationRepository synchronisationRepository, ErrorRecorder errorRecorder) {
        this.updateService = updateService;
        this.synchronisationRepository = synchronisationRepository;
        this.errorRecorder = errorRecorder;
    }

    @Override
    public void synchronise() {
        try {
            trySynchronisationFallibly();
        } catch (SubsystemException e) {
            LOG.warn("failed to synchronise", e);
            errorRecorder.recordSynchronisationError(e);
        }
    }

    @Override
    public void synchroniseFull() {
        try {
            synchronisationRepository.writeUpdates(emptyList());
            trySynchronisationFallibly();
        } catch (SubsystemException e) {
            LOG.warn("failed to synchronise", e);
            errorRecorder.recordSynchronisationError(e);
        }
    }

    private void trySynchronisationFallibly() {
        EntitySynchroniser entitySynchroniser = new EntitySynchroniser();
        EntityInitialiser entityInitialiser = new EntityInitialiser();

        List<Update> serverUpdateList = updateService.getUpdates();
        Map<EntityType, Instant> serverUpdates = serverUpdateList
                .stream()
                .collect(groupingBy(Update::table, reducing(Instant.MIN, Update::lastUpdate, Comparators::max)));
        Map<EntityType, Instant> localUpdates = synchronisationRepository.getUpdates()
                .stream()
                .collect(groupingBy(Update::table, reducing(Instant.MIN, Update::lastUpdate, Comparators::max)));

        for (EntityType entityType : serverUpdates.keySet()) {
            Instant localState = localUpdates.computeIfAbsent(entityType, v -> Instant.MIN);
            Instant serverState = serverUpdates.get(entityType);

            if (localState.equals(Instant.MIN))
                entityInitialiser.visit(entityType, new InstantInterval(localState, serverState));
            else if (localState.isBefore(serverState))
                entitySynchroniser.visit(entityType, new InstantInterval(localState, serverState));
        }

        synchronisationRepository.writeUpdates(serverUpdateList);
    }

    private static final class InstantInterval {
        private final Instant startingFrom;
        private final Instant upUntil;

        private InstantInterval(Instant startingFrom, Instant upUntil) {
            this.startingFrom = startingFrom;
            this.upUntil = upUntil;
        }

        private Instant startingFrom() {
            return startingFrom;
        }

        private Instant upUntil() {
            return upUntil;
        }
    }

    private final class EntitySynchroniser implements EntityType.Visitor<InstantInterval, Void> {

        @Override
        public Void visit(EntityType item, InstantInterval input) {
            LOG.info("synchronising " + item);
            return EntityType.Visitor.super.visit(item, input);
        }

        @Override
        public Void location(InstantInterval input) {
            List<LocationForSynchronisation> items = updateService.getLocations(input.startingFrom(), input.upUntil());
            synchronisationRepository.writeLocations(items);
            return null;
        }

        @Override
        public Void user(InstantInterval input) {
            List<UserForSynchronisation> items = updateService.getUsers(input.startingFrom(), input.upUntil());
            synchronisationRepository.writeUsers(items);
            return null;
        }

        @Override
        public Void userDevice(InstantInterval input) {
            List<UserDeviceForSynchronisation> items = updateService.getUserDevices(input.startingFrom(), input.upUntil());
            synchronisationRepository.writeUserDevices(items);
            return null;
        }

        @Override
        public Void food(InstantInterval input) {
            List<FoodForSynchronisation> items = updateService.getFood(input.startingFrom(), input.upUntil());
            synchronisationRepository.writeFood(items);
            return null;
        }

        @Override
        public Void eanNumber(InstantInterval input) {
            List<EanNumberForSynchronisation> items = updateService.getEanNumbers(input.startingFrom(), input.upUntil());
            synchronisationRepository.writeEanNumbers(items);
            return null;
        }

        @Override
        public Void foodItem(InstantInterval input) {
            List<FoodItemForSynchronisation> items = updateService.getFoodItems(input.startingFrom(), input.upUntil());
            synchronisationRepository.writeFoodItems(items);
            return null;
        }

        @Override
        public Void unit(InstantInterval input) {
            List<UnitForSynchronisation> items = updateService.getUnits(input.startingFrom(), input.upUntil());
            synchronisationRepository.writeUnits(items);
            return null;
        }

        @Override
        public Void scaledUnit(InstantInterval input) {
            List<ScaledUnitForSynchronisation> items = updateService.getScaledUnits(input.startingFrom(), input.upUntil());
            synchronisationRepository.writeScaledUnits(items);
            return null;
        }

        @Override
        public Void recipe(InstantInterval input) {
            List<RecipeForSynchronisation> items = updateService.getRecipes(input.startingFrom(), input.upUntil());
            synchronisationRepository.writeRecipes(items);
            return null;
        }

        @Override
        public Void recipeIngredient(InstantInterval input) {
            List<RecipeIngredientForSynchronisation> items = updateService.getRecipeIngredients(input.startingFrom(), input.upUntil());
            synchronisationRepository.writeRecipeIngredients(items);
            return null;
        }

        @Override
        public Void recipeProduct(InstantInterval input) {
            List<RecipeProductForSynchronisation> items = updateService.getRecipeProducts(input.startingFrom(), input.upUntil());
            synchronisationRepository.writeRecipeProducts(items);
            return null;
        }
    }

    private final class EntityInitialiser implements EntityType.Visitor<InstantInterval, Void> {

        @Override
        public Void visit(EntityType item, InstantInterval input) {
            LOG.info("initialising " + item);
            return EntityType.Visitor.super.visit(item, input);
        }

        @Override
        public Void location(InstantInterval input) {
            List<LocationForSynchronisation> items = updateService.getLocations(input.startingFrom(), input.upUntil());
            synchronisationRepository.initialiseLocations(items);
            return null;
        }

        @Override
        public Void user(InstantInterval input) {
            List<UserForSynchronisation> items = updateService.getUsers(input.startingFrom(), input.upUntil());
            synchronisationRepository.initialiseUsers(items);
            return null;
        }

        @Override
        public Void userDevice(InstantInterval input) {
            List<UserDeviceForSynchronisation> items = updateService.getUserDevices(input.startingFrom(), input.upUntil());
            synchronisationRepository.initialiseUserDevices(items);
            return null;
        }

        @Override
        public Void food(InstantInterval input) {
            List<FoodForSynchronisation> items = updateService.getFood(input.startingFrom(), input.upUntil());
            synchronisationRepository.initialiseFood(items);
            return null;
        }

        @Override
        public Void eanNumber(InstantInterval input) {
            List<EanNumberForSynchronisation> items = updateService.getEanNumbers(input.startingFrom(), input.upUntil());
            synchronisationRepository.initialiseEanNumbers(items);
            return null;
        }

        @Override
        public Void foodItem(InstantInterval input) {
            List<FoodItemForSynchronisation> items = updateService.getFoodItems(input.startingFrom(), input.upUntil());
            synchronisationRepository.initialiseFoodItems(items);
            return null;
        }

        @Override
        public Void unit(InstantInterval input) {
            List<UnitForSynchronisation> items = updateService.getUnits(input.startingFrom(), input.upUntil());
            synchronisationRepository.initialiseUnits(items);
            return null;
        }

        @Override
        public Void scaledUnit(InstantInterval input) {
            List<ScaledUnitForSynchronisation> items = updateService.getScaledUnits(input.startingFrom(), input.upUntil());
            synchronisationRepository.initialiseScaledUnits(items);
            return null;
        }

        @Override
        public Void recipe(InstantInterval input) {
            List<RecipeForSynchronisation> items = updateService.getRecipes(input.startingFrom(), input.upUntil());
            synchronisationRepository.initialiseRecipes(items);
            return null;
        }

        @Override
        public Void recipeIngredient(InstantInterval input) {
            List<RecipeIngredientForSynchronisation> items = updateService.getRecipeIngredients(input.startingFrom(), input.upUntil());
            synchronisationRepository.initialiseRecipeIngredients(items);
            return null;
        }

        @Override
        public Void recipeProduct(InstantInterval input) {
            List<RecipeProductForSynchronisation> items = updateService.getRecipeProducts(input.startingFrom(), input.upUntil());
            synchronisationRepository.initialiseRecipeProducts(items);
            return null;
        }
    }
}
