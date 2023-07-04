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
import de.njsm.stocks.client.business.entities.EntityType;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.event.ActivityEvent;
import de.njsm.stocks.client.execution.Scheduler;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

abstract class BaseEventInteractorImpl implements EventInteractor {

    final EventRepository repository;

    final ActivityEventFactory eventFactory;

    final Localiser localiser;

    private final Scheduler scheduler;

    BaseEventInteractorImpl(EventRepository repository, ActivityEventFactory eventFactory, Localiser localiser, Scheduler scheduler) {
        this.repository = repository;
        this.eventFactory = eventFactory;
        this.localiser = localiser;
        this.scheduler = scheduler;
    }

    @Override
    public Observable<LocalDateTime> getNewEventNotifier() {
        return repository.getNewEventNotifier(getRelevantEntities())
                .map(localiser::toLocalDateTime);
    }

    <T extends EventFeedItem<E>, E extends Entity<E>>
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

    protected List<ActivityEvent> sortEvents(List<List<ActivityEvent>> lists) {
        List<ActivityEvent> result = new ArrayList<>();
        lists.forEach(result::addAll);
        result.sort(comparing(ActivityEvent::timeOccurred).reversed());
        return result;
    }

    abstract List<EntityType> getRelevantEntities();

    Single<Optional<Instant>> getPreviousDay(LocalDate day) {
        return repository.getNextDayContainingEvents(localiser.toInstant(day), getRelevantEntities())
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty())
                .subscribeOn(scheduler.into());
    }

    Single<Optional<Instant>> getNextDay(LocalDate day) {
        return repository.getPreviousDayContainingEvents(localiser.toInstant(day), getRelevantEntities())
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty())
                .subscribeOn(scheduler.into());
    }

    Single<ActivityEventPage> toEventPage(LocalDate day, Single<List<ActivityEvent>> events) {
        return Single.zip(
                events,
                getNextDay(day),
                getPreviousDay(day), (e, p, n) ->
                        ActivityEventPage.create(e, p.map(localiser::toLocalDate), n.map(localiser::toLocalDate)));
    }
}
