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
import de.njsm.stocks.client.business.entities.event.ActivityEvent;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

import javax.inject.Inject;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
                .map(feedItems -> {
                    Map<Instant, List<LocationEventFeedItem>> groupedItems = feedItems.stream()
                            .collect(groupingBy(LocationEventFeedItem::transactionTimeStart,
                                    () -> new TreeMap<>(Comparator.<Instant, Instant>comparing(x -> x).reversed())
                                    , toList()));

                    return groupedItems.values()
                            .stream()
                            .map(eventFactory::getLocationEventFrom)
                            .collect(toList());
                });
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
}
