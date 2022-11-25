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
                entityInitialiser.visit(entityType, localState);
            else if (localState.isBefore(serverState))
                entitySynchroniser.visit(entityType, localState);
        }

        synchronisationRepository.writeUpdates(serverUpdateList);
    }

    private final class EntitySynchroniser implements EntityType.Visitor<Instant, Void> {

        @Override
        public Void visit(EntityType item, Instant input) {
            LOG.info("synchronising " + item);
            return EntityType.Visitor.super.visit(item, input);
        }

        @Override
        public Void location(Instant startingFrom) {
            List<LocationForSynchronisation> items = updateService.getLocations(startingFrom);
            synchronisationRepository.writeLocations(items);
            return null;
        }

        @Override
        public Void user(Instant input) {
            List<UserForSynchronisation> items = updateService.getUsers(input);
            synchronisationRepository.writeUsers(items);
            return null;
        }

        @Override
        public Void userDevice(Instant input) {
            List<UserDeviceForSynchronisation> items = updateService.getUserDevices(input);
            synchronisationRepository.writeUserDevices(items);
            return null;
        }

        @Override
        public Void food(Instant input) {
            List<FoodForSynchronisation> items = updateService.getFood(input);
            synchronisationRepository.writeFood(items);
            return null;
        }

        @Override
        public Void eanNumber(Instant input) {
            List<EanNumberForSynchronisation> items = updateService.getEanNumbers(input);
            synchronisationRepository.writeEanNumbers(items);
            return null;
        }

        @Override
        public Void foodItem(Instant input) {
            List<FoodItemForSynchronisation> items = updateService.getFoodItems(input);
            synchronisationRepository.writeFoodItems(items);
            return null;
        }

        @Override
        public Void unit(Instant input) {
            List<UnitForSynchronisation> items = updateService.getUnits(input);
            synchronisationRepository.writeUnits(items);
            return null;
        }

        @Override
        public Void scaledUnit(Instant input) {
            List<ScaledUnitForSynchronisation> items = updateService.getScaledUnits(input);
            synchronisationRepository.writeScaledUnits(items);
            return null;
        }

        @Override
        public Void recipe(Instant input) {
            List<RecipeForSynchronisation> items = updateService.getRecipes(input);
            synchronisationRepository.writeRecipes(items);
            return null;
        }

        @Override
        public Void recipeIngredient(Instant input) {
            List<RecipeIngredientForSynchronisation> items = updateService.getRecipeIngredients(input);
            synchronisationRepository.writeRecipeIngredients(items);
            return null;
        }

        @Override
        public Void recipeProduct(Instant input) {
            List<RecipeProductForSynchronisation> items = updateService.getRecipeProducts(input);
            synchronisationRepository.writeRecipeProducts(items);
            return null;
        }
    }

    private final class EntityInitialiser implements EntityType.Visitor<Instant, Void> {

        @Override
        public Void visit(EntityType item, Instant input) {
            LOG.info("initialising " + item);
            return EntityType.Visitor.super.visit(item, input);
        }

        @Override
        public Void location(Instant startingFrom) {
            List<LocationForSynchronisation> items = updateService.getLocations(startingFrom);
            synchronisationRepository.initialiseLocations(items);
            return null;
        }

        @Override
        public Void user(Instant input) {
            List<UserForSynchronisation> items = updateService.getUsers(input);
            synchronisationRepository.initialiseUsers(items);
            return null;
        }

        @Override
        public Void userDevice(Instant input) {
            List<UserDeviceForSynchronisation> items = updateService.getUserDevices(input);
            synchronisationRepository.initialiseUserDevices(items);
            return null;
        }

        @Override
        public Void food(Instant input) {
            List<FoodForSynchronisation> items = updateService.getFood(input);
            synchronisationRepository.initialiseFood(items);
            return null;
        }

        @Override
        public Void eanNumber(Instant input) {
            List<EanNumberForSynchronisation> items = updateService.getEanNumbers(input);
            synchronisationRepository.initialiseEanNumbers(items);
            return null;
        }

        @Override
        public Void foodItem(Instant input) {
            List<FoodItemForSynchronisation> items = updateService.getFoodItems(input);
            synchronisationRepository.initialiseFoodItems(items);
            return null;
        }

        @Override
        public Void unit(Instant input) {
            List<UnitForSynchronisation> items = updateService.getUnits(input);
            synchronisationRepository.initialiseUnits(items);
            return null;
        }

        @Override
        public Void scaledUnit(Instant input) {
            List<ScaledUnitForSynchronisation> items = updateService.getScaledUnits(input);
            synchronisationRepository.initialiseScaledUnits(items);
            return null;
        }

        @Override
        public Void recipe(Instant input) {
            List<RecipeForSynchronisation> items = updateService.getRecipes(input);
            synchronisationRepository.initialiseRecipes(items);
            return null;
        }

        @Override
        public Void recipeIngredient(Instant input) {
            List<RecipeIngredientForSynchronisation> items = updateService.getRecipeIngredients(input);
            synchronisationRepository.initialiseRecipeIngredients(items);
            return null;
        }

        @Override
        public Void recipeProduct(Instant input) {
            List<RecipeProductForSynchronisation> items = updateService.getRecipeProducts(input);
            synchronisationRepository.initialiseRecipeProducts(items);
            return null;
        }
    }
}
