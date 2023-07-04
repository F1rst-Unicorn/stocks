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

package de.njsm.stocks.client.database;

import de.njsm.stocks.client.business.entities.EntityType;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.Location;
import de.njsm.stocks.client.business.event.*;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.util.Collections.emptyList;

class EventRepositoryImpl implements EventRepository {

    private final EventDao eventDao;

    private final NextDayVisitorFactory nextDayVisitorFactory;

    private final PreviousDayVisitorFactory previousDayVisitorFactory;

    @Inject
    EventRepositoryImpl(EventDao eventDao) {
        this.eventDao = eventDao;
        nextDayVisitorFactory = new NextDayVisitorFactory();
        previousDayVisitorFactory = new PreviousDayVisitorFactory();
    }

    @Override
    public Single<List<LocationEventFeedItem>> getLocationFeed(Instant day) {
        return eventDao.getLocationEvents(day, day.plus(1, ChronoUnit.DAYS))
                .first(emptyList());
    }

    @Override
    public Single<List<UnitEventFeedItem>> getUnitFeed(Instant day) {
        return eventDao.getUnitEvents(day, day.plus(1, ChronoUnit.DAYS))
                .first(emptyList());
    }

    @Override
    public Single<List<UserEventFeedItem>> getUserFeed(Instant day) {
        return eventDao.getUserEvents(day, day.plus(1, ChronoUnit.DAYS))
                .first(emptyList());
    }

    @Override
    public Single<List<UserDeviceEventFeedItem>> getUserDeviceFeed(Instant day) {
        return eventDao.getUserDeviceEvents(day, day.plus(1, ChronoUnit.DAYS))
                .first(emptyList());
    }

    @Override
    public Single<List<ScaledUnitEventFeedItem>> getScaledUnitFeed(Instant day) {
        return eventDao.getScaledUnitEvents(day, day.plus(1, ChronoUnit.DAYS))
                .first(emptyList());
    }

    @Override
    public Single<List<FoodEventFeedItem>> getFoodFeed(Instant day) {
        return eventDao.getFoodEvents(day, day.plus(1, ChronoUnit.DAYS))
                .first(emptyList());
    }

    @Override
    public Single<List<FoodItemEventFeedItem>> getFoodItemFeed(Instant day) {
        return eventDao.getFoodItemEvents(day, day.plus(1, ChronoUnit.DAYS))
                .first(emptyList());
    }

    @Override
    public Single<List<EanNumberEventFeedItem>> getEanNumberFeed(Instant day) {
        return eventDao.getEanNumberEvents(day, day.plus(1, ChronoUnit.DAYS))
                .first(emptyList());
    }

    @Override
    public Single<List<LocationEventFeedItem>> getLocationEventsOf(Id<Location> location, Instant day) {
        return eventDao.getLocationEventsOf(location.id(), day, day.plus(1, ChronoUnit.DAYS))
                .first(emptyList());
    }

    @Override
    public Single<List<FoodItemEventFeedItem>> getFoodItemEventsInvolving(Id<Location> location, Instant day) {
        return eventDao.getFoodItemEventsInvolving(location.id(), day, day.plus(1, ChronoUnit.DAYS))
                .first(emptyList());
    }

    @Override
    public Single<List<EanNumberEventFeedItem>> getEanNumberEventsOf(Id<Food> food, Instant day) {
        return eventDao.getEanNumberEventsOf(food.id(), day, day.plus(1, ChronoUnit.DAYS))
                .first(emptyList());
    }

    @Override
    public Single<List<FoodEventFeedItem>> getFoodEventsOf(Id<Food> food, Instant day) {
        return eventDao.getFoodEventsOf(food.id(), day, day.plus(1, ChronoUnit.DAYS))
                .first(emptyList());
    }

    @Override
    public Single<List<FoodItemEventFeedItem>> getFoodItemEventsOf(Id<Food> food, Instant day) {
        return eventDao.getFoodItemEventsOf(food.id(), day, day.plus(1, ChronoUnit.DAYS))
                .first(emptyList());
    }

    @Override
    public Maybe<Instant> getPreviousDayContainingEvents(Instant day, List<EntityType> relevantEntities, EventKeyHint hint) {
        return previousDayVisitorFactory.visit(hint, new Args(day, relevantEntities))
                .getPreviousDayContainingEvents();
    }

    @Override
    public Maybe<Instant> getNextDayContainingEvents(Instant day, List<EntityType> relevantEntities, EventKeyHint hint) {
        return nextDayVisitorFactory.visit(hint, new Args(day.plus(1, ChronoUnit.DAYS), relevantEntities))
                .getNextDayContainingEvents();
    }

    @Override
    public Observable<Instant> getNewEventNotifier(List<EntityType> relevantEntities) {
        return eventDao.getLatestUpdateTimestamp(relevantEntities)
                .distinctUntilChanged()
                .skip(1);
    }

    private static class Args {
        private final Instant day;
        private final List<EntityType> relevantEntities;
        Args(Instant day, List<EntityType> relevantEntities) {
            this.day = day;
            this.relevantEntities = relevantEntities;
        }
    }

    private final class NextDayVisitorFactory implements EventKeyHint.Visitor<Args, NextDayContainingEventsVisitor> {
        @Override
        public NextDayContainingEventsVisitor none(EventKeyHint.None none, Args input) {
            return new NextDayContainingEventsVisitor(eventDao, input.relevantEntities, input.day);
        }

        @Override
        public NextDayContainingEventsVisitor location(EventKeyHint.Location location, Args input) {
            return new NextDayContainingEventsWithLocationVisitor(eventDao, input.relevantEntities, input.day, location.id());
        }

        @Override
        public NextDayContainingEventsVisitor food(EventKeyHint.Food food, Args input) {
            return new NextDayContainingEventsWithFoodVisitor(eventDao, input.relevantEntities, input.day, food.id());
        }
    }

    private final class PreviousDayVisitorFactory implements EventKeyHint.Visitor<Args, PreviousDayContainingEventsVisitor> {
        @Override
        public PreviousDayContainingEventsVisitor none(EventKeyHint.None none, Args input) {
            return new PreviousDayContainingEventsVisitor(eventDao, input.relevantEntities, input.day);
        }

        @Override
        public PreviousDayContainingEventsVisitor location(EventKeyHint.Location location, Args input) {
            return new PreviousDayContainingEventsWithLocationVisitor(eventDao, input.relevantEntities, input.day, location.id());
        }

        @Override
        public PreviousDayContainingEventsVisitor food(EventKeyHint.Food food, Args input) {
            return new PreviousDayContainingEventsWithFoodVisitor(eventDao, input.relevantEntities, input.day, food.id());
        }
    }
}
