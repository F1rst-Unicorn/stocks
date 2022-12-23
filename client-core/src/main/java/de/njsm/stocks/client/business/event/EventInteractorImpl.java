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
import de.njsm.stocks.client.business.entities.Entity;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.event.ActivityEvent;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

import javax.inject.Inject;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class EventInteractorImpl implements EventInteractor {

    private final EventRepository repository;

    private final ActivityEventFactory eventFactory;

    private final Localiser localiser;

    @Inject
    public EventInteractorImpl(EventRepository repository, ActivityEventFactory eventFactory, Localiser localiser) {
        this.repository = repository;
        this.eventFactory = eventFactory;
        this.localiser = localiser;
    }

    @Override
    public Single<List<ActivityEvent>> getEventsOf(LocalDate day) {
        return repository.getLocationFeed(localiser.toInstant(day))
                .map(v -> transformToEvents(v, eventFactory::getLocationEventFrom))
                .mergeWith(repository.getUnitFeed(localiser.toInstant(day))
                        .map(v -> transformToEvents(v, eventFactory::getUnitEventFrom)))
                .mergeWith(repository.getUserFeed(localiser.toInstant(day))
                        .map(v -> transformToEvents(v, eventFactory::getUserEventFrom)))
                .mergeWith(repository.getUserDeviceFeed(localiser.toInstant(day))
                        .map(v -> transformToEvents(v, eventFactory::getUserDeviceEventFrom)))
                .mergeWith(repository.getScaledUnitFeed(localiser.toInstant(day))
                        .map(v -> transformToEvents(v, eventFactory::getScaledUnitEventFrom)))
                .mergeWith(repository.getFoodFeed(localiser.toInstant(day))
                        .map(v -> transformToEvents(v, eventFactory::getFoodEventFrom)))
                .mergeWith(repository.getFoodItemFeed(localiser.toInstant(day))
                        .map(v -> transformToEvents(v, eventFactory::getFoodItemEventFrom)))

                .buffer(7) // align with number of merged feeds above
                .map(lists -> {
                    List<ActivityEvent> result = new ArrayList<>();
                    lists.forEach(result::addAll);
                    result.sort(comparing(ActivityEvent::timeOccurred).reversed());
                    return result;
                })
                .first(emptyList());
    }

    @Override
    public Observable<LocalDateTime> getNewEventNotifier() {
        return repository.getNewEventNotifier()
                .map(localiser::toLocalDateTime);
    }

    @Override
    public Single<LocalDate> getOldestEventTime() {
        return repository.getOldestEventTime()
                .map(localiser::toLocalDate);
    }

    private <T extends EventFeedItem<E>, E extends Entity<E>>
    List<ActivityEvent> transformToEvents(List<T> feedItems, Function<List<T>, ActivityEvent> mapper) {
        Map<Instant, Map<Id<E>, List<T>>> groupedItems = feedItems.stream()
                .collect(groupingBy(T::transactionTimeStart,
                        () -> new TreeMap<>(Comparator.<Instant, Instant>comparing(x -> x).reversed()),
                        groupingBy(T::id, toList())));

        return groupedItems.values()
                .stream()
                .flatMap(v -> v.values()
                        .stream()
                        .map(mapper))
                .collect(toList());
    }
}
