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
import de.njsm.stocks.client.business.entities.EntityType;
import de.njsm.stocks.client.business.entities.LocationForSynchronisation;
import de.njsm.stocks.client.business.entities.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

class SynchroniseInteractorImpl implements SynchroniseInteractor {

    private static final Logger LOG = LoggerFactory.getLogger(SynchroniseInteractorImpl.class);

    private final UpdateService updateService;

    private final SynchronisationRepository synchronisationRepository;

    @Inject
    SynchroniseInteractorImpl(UpdateService updateService, SynchronisationRepository synchronisationRepository) {
        this.updateService = updateService;
        this.synchronisationRepository = synchronisationRepository;
    }

    @Override
    public void synchronise() {
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

    private final class EntitySynchroniser extends EntityType.Visitor<Instant, Void> {

        @Override
        public Void visit(EntityType item, Instant input) {
            LOG.info("synchronising " + item);
            return super.visit(item, input);
        }

        @Override
        public Void location(Instant startingFrom) {
            List<LocationForSynchronisation> items = updateService.getLocations(startingFrom);
            synchronisationRepository.writeLocations(items);
            return null;
        }
    }

    private final class EntityInitialiser extends EntityType.Visitor<Instant, Void> {

        @Override
        public Void visit(EntityType item, Instant input) {
            LOG.info("initialising " + item);
            return super.visit(item, input);
        }

        @Override
        public Void location(Instant startingFrom) {
            List<LocationForSynchronisation> items = updateService.getLocations(startingFrom);
            synchronisationRepository.initialiseLocations(items);
            return null;
        }
    }
}
