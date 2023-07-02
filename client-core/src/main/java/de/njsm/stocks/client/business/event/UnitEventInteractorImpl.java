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

package de.njsm.stocks.client.business.event;

import de.njsm.stocks.client.business.Localiser;
import de.njsm.stocks.client.business.entities.EntityType;
import de.njsm.stocks.client.business.entities.event.ActivityEvent;
import de.njsm.stocks.client.execution.Scheduler;
import io.reactivex.rxjava3.core.Single;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;

public class UnitEventInteractorImpl extends BaseEventInteractorImpl implements UnitEventInteractor {

    @Inject
    public UnitEventInteractorImpl(EventRepository repository, ActivityEventFactory eventFactory, Localiser localiser, Scheduler scheduler) {
        super(repository, eventFactory, localiser, scheduler);
    }

    @Override
    List<EntityType> getRelevantEntities() {
        return List.of(
                EntityType.UNIT,
                EntityType.SCALED_UNIT
        );
    }

    @Override
    public Single<ActivityEventPage> getEventsOf(LocalDate day) {
        Single<List<ActivityEvent>> events = repository.getUnitFeed(localiser.toInstant(day))
                .map(v -> transformToEvents(v, eventFactory::getUnitEventFrom))
                .mergeWith(repository.getScaledUnitFeed(localiser.toInstant(day))
                        .map(v -> transformToEvents(v, eventFactory::getScaledUnitEventFrom)))

                .buffer(getRelevantEntities().size())
                .map(this::sortEvents)
                .first(emptyList());

        return toEventPage(day, events);
    }
}
