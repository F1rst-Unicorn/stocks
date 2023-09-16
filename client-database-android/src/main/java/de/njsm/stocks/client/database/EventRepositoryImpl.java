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
    public Single<List<LocationEventFeedItem>> getLocationFeed(EventKeyHint hint, Instant day) {
        return new EventKeyHint.Visitor<Void, Single<List<LocationEventFeedItem>>>() {
            @Override
            public Single<List<LocationEventFeedItem>> none(EventKeyHint.None none, Void input) {
                return eventDao.getLocationEvents(day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }

            @Override
            public Single<List<LocationEventFeedItem>> location(EventKeyHint.Location location, Void input) {
                return eventDao.getLocationEventsOf(location.id().id(), day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }

            @Override
            public Single<List<LocationEventFeedItem>> user(EventKeyHint.User user, Void input) {
                return eventDao.getLocationEventsOfInitiatorUser(user.id().id(), day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }

            @Override
            public Single<List<LocationEventFeedItem>> userDevice(EventKeyHint.UserDevice userDevice, Void input) {
                return eventDao.getLocationEventsOfInitiator(userDevice.id().id(), day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }
        }.visit(hint, null);
    }

    @Override
    public Single<List<UnitEventFeedItem>> getUnitFeed(EventKeyHint hint, Instant day) {
        return new EventKeyHint.Visitor<Void, Single<List<UnitEventFeedItem>>>() {
            @Override
            public Single<List<UnitEventFeedItem>> none(EventKeyHint.None none, Void input) {
                return eventDao.getUnitEvents(day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }

            @Override
            public Single<List<UnitEventFeedItem>> user(EventKeyHint.User user, Void input) {
                return eventDao.getUnitEventsOfInitiatorUser(user.id().id(), day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }

            @Override
            public Single<List<UnitEventFeedItem>> userDevice(EventKeyHint.UserDevice userDevice, Void input) {
                return eventDao.getUnitEventsOfInitiator(userDevice.id().id(), day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }
        }.visit(hint, null);
    }

    @Override
    public Single<List<UserEventFeedItem>> getUserFeed(EventKeyHint hint, Instant day) {
        return new EventKeyHint.Visitor<Void, Single<List<UserEventFeedItem>>>() {
            @Override
            public Single<List<UserEventFeedItem>> none(EventKeyHint.None none, Void input) {
                return eventDao.getUserEvents(day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }

            @Override
            public Single<List<UserEventFeedItem>> user(EventKeyHint.User user, Void input) {
                return eventDao.getUserEventsOfInitiatorUser(user.id().id(), day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }

            @Override
            public Single<List<UserEventFeedItem>> userDevice(EventKeyHint.UserDevice userDevice, Void input) {
                return eventDao.getUserEventsOfInitiator(userDevice.id().id(), day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }
        }.visit(hint, null);
    }

    @Override
    public Single<List<UserDeviceEventFeedItem>> getUserDeviceFeed(EventKeyHint hint, Instant day) {
        return new EventKeyHint.Visitor<Void, Single<List<UserDeviceEventFeedItem>>>() {
            @Override
            public Single<List<UserDeviceEventFeedItem>> none(EventKeyHint.None none, Void input) {
                return eventDao.getUserDeviceEvents(day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }

            @Override
            public Single<List<UserDeviceEventFeedItem>> user(EventKeyHint.User user, Void input) {
                return eventDao.getUserDeviceEventsOfInitiatorUser(user.id().id(), day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }

            @Override
            public Single<List<UserDeviceEventFeedItem>> userDevice(EventKeyHint.UserDevice userDevice, Void input) {
                return eventDao.getUserDeviceEventsOfInitiator(userDevice.id().id(), day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }
        }.visit(hint, null);
    }

    @Override
    public Single<List<ScaledUnitEventFeedItem>> getScaledUnitFeed(EventKeyHint hint, Instant day) {
        return new EventKeyHint.Visitor<Void, Single<List<ScaledUnitEventFeedItem>>>() {
            @Override
            public Single<List<ScaledUnitEventFeedItem>> none(EventKeyHint.None none, Void input) {
                return eventDao.getScaledUnitEvents(day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }

            @Override
            public Single<List<ScaledUnitEventFeedItem>> user(EventKeyHint.User user, Void input) {
                return eventDao.getScaledUnitEventsOfInitiatorUser(user.id().id(), day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }

            @Override
            public Single<List<ScaledUnitEventFeedItem>> userDevice(EventKeyHint.UserDevice userDevice, Void input) {
                return eventDao.getScaledUnitEventsOfInitiator(userDevice.id().id(), day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }
        }.visit(hint, null);
    }

    @Override
    public Single<List<FoodEventFeedItem>> getFoodFeed(EventKeyHint hint, Instant day) {
        return new EventKeyHint.Visitor<Void, Single<List<FoodEventFeedItem>>>() {
            @Override
            public Single<List<FoodEventFeedItem>> none(EventKeyHint.None none, Void input) {
                return eventDao.getFoodEvents(day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }

            @Override
            public Single<List<FoodEventFeedItem>> food(EventKeyHint.Food food, Void input) {
                return eventDao.getFoodEventsOf(food.id().id(), day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }

            @Override
            public Single<List<FoodEventFeedItem>> user(EventKeyHint.User user, Void input) {
                return eventDao.getFoodEventsOfInitiatorUser(user.id().id(), day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }

            @Override
            public Single<List<FoodEventFeedItem>> userDevice(EventKeyHint.UserDevice userDevice, Void input) {
                return eventDao.getFoodEventsOfInitiator(userDevice.id().id(), day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }
        }.visit(hint, null);
    }

    @Override
    public Single<List<FoodItemEventFeedItem>> getFoodItemFeed(EventKeyHint hint, Instant day) {
        return new EventKeyHint.Visitor<Void, Single<List<FoodItemEventFeedItem>>>() {
            @Override
            public Single<List<FoodItemEventFeedItem>> none(EventKeyHint.None none, Void input) {
                return eventDao.getFoodItemEvents(day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }

            @Override
            public Single<List<FoodItemEventFeedItem>> location(EventKeyHint.Location location, Void input) {
                return eventDao.getFoodItemEventsInvolving(location.id().id(), day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }

            @Override
            public Single<List<FoodItemEventFeedItem>> food(EventKeyHint.Food food, Void input) {
                return eventDao.getFoodItemEventsOf(food.id().id(), day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }

            @Override
            public Single<List<FoodItemEventFeedItem>> user(EventKeyHint.User user, Void input) {
                return eventDao.getFoodItemEventsOfInitiatorUser(user.id().id(), day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }

            @Override
            public Single<List<FoodItemEventFeedItem>> userDevice(EventKeyHint.UserDevice userDevice, Void input) {
                return eventDao.getFoodItemEventsOfInitiator(userDevice.id().id(), day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }
        }.visit(hint, null);
    }

    @Override
    public Single<List<EanNumberEventFeedItem>> getEanNumberFeed(EventKeyHint hint, Instant day) {
        return new EventKeyHint.Visitor<Void, Single<List<EanNumberEventFeedItem>>>() {
            @Override
            public Single<List<EanNumberEventFeedItem>> none(EventKeyHint.None none, Void input) {
                return eventDao.getEanNumberEvents(day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }

            @Override
            public Single<List<EanNumberEventFeedItem>> food(EventKeyHint.Food food, Void input) {
                return eventDao.getEanNumberEventsOfFood(food.id().id(), day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }

            @Override
            public Single<List<EanNumberEventFeedItem>> user(EventKeyHint.User user, Void input) {
                return eventDao.getEanNumberEventsOfInitiatorUser(user.id().id(), day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }

            @Override
            public Single<List<EanNumberEventFeedItem>> userDevice(EventKeyHint.UserDevice userDevice, Void input) {
                return eventDao.getEanNumberEventsOfInitiator(userDevice.id().id(), day, day.plus(1, ChronoUnit.DAYS))
                        .first(emptyList());
            }
        }.visit(hint, null);
    }

    @Override
    public Maybe<Instant> getPreviousDayContainingEvents(Instant day, List<EntityType> relevantEntities, EventKeyHint hint) {
        return previousDayVisitorFactory.visit(hint, new Args(day, relevantEntities))
                .getNextDayContainingEvents();
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
            return NextDayContainingEventsVisitor.intoFuture(eventDao, input.relevantEntities, input.day);
        }

        @Override
        public NextDayContainingEventsVisitor location(EventKeyHint.Location location, Args input) {
            return NextDayContainingEventsWithLocationVisitor.intoFuture(eventDao, input.relevantEntities, input.day, location.id());
        }

        @Override
        public NextDayContainingEventsVisitor food(EventKeyHint.Food food, Args input) {
            return NextDayContainingEventsWithFoodVisitor.intoFuture(eventDao, input.relevantEntities, input.day, food.id());
        }

        @Override
        public NextDayContainingEventsVisitor user(EventKeyHint.User user, Args input) {
            return NextDayContainingEventsWithUserVisitor.intoFuture(eventDao, input.relevantEntities, input.day, user.id());
        }

        @Override
        public NextDayContainingEventsVisitor userDevice(EventKeyHint.UserDevice userDevice, Args input) {
            return NextDayContainingEventsWithUserDeviceVisitor.intoFuture(eventDao, input.relevantEntities, input.day, userDevice.id());
        }
    }

    private final class PreviousDayVisitorFactory implements EventKeyHint.Visitor<Args, NextDayContainingEventsVisitor> {
        @Override
        public NextDayContainingEventsVisitor none(EventKeyHint.None none, Args input) {
            return NextDayContainingEventsVisitor.intoPast(eventDao, input.relevantEntities, input.day);
        }

        @Override
        public NextDayContainingEventsVisitor location(EventKeyHint.Location location, Args input) {
            return NextDayContainingEventsWithLocationVisitor.intoPast(eventDao, input.relevantEntities, input.day, location.id());
        }

        @Override
        public NextDayContainingEventsVisitor food(EventKeyHint.Food food, Args input) {
            return NextDayContainingEventsWithFoodVisitor.intoPast(eventDao, input.relevantEntities, input.day, food.id());
        }

        @Override
        public NextDayContainingEventsVisitor user(EventKeyHint.User user, Args input) {
            return NextDayContainingEventsWithUserVisitor.intoPast(eventDao, input.relevantEntities, input.day, user.id());
        }

        @Override
        public NextDayContainingEventsVisitor userDevice(EventKeyHint.UserDevice userDevice, Args input) {
            return NextDayContainingEventsWithUserDeviceVisitor.intoPast(eventDao, input.relevantEntities, input.day, userDevice.id());
        }
    }
}
