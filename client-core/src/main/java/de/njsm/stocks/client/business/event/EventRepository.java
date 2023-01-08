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

import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.Location;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

import java.time.Instant;
import java.util.List;

public interface EventRepository {

    Observable<Instant> getNewEventNotifier();

    Single<List<LocationEventFeedItem>> getLocationFeed(Instant day);

    Single<List<UnitEventFeedItem>> getUnitFeed(Instant day);

    Single<List<UserEventFeedItem>> getUserFeed(Instant day);

    Single<List<UserDeviceEventFeedItem>> getUserDeviceFeed(Instant day);

    Single<List<ScaledUnitEventFeedItem>> getScaledUnitFeed(Instant day);

    Single<List<FoodEventFeedItem>> getFoodFeed(Instant day);

    Single<List<FoodItemEventFeedItem>> getFoodItemFeed(Instant day);

    Single<List<EanNumberEventFeedItem>> getEanNumberFeed(Instant day);

    Single<List<LocationEventFeedItem>> getLocationEventsOf(Id<Location> location, Instant day);

    Single<List<FoodItemEventFeedItem>> getFoodItemEventsInvolving(Id<Location> location, Instant day);

    Single<List<EanNumberEventFeedItem>> getEanNumberEventsOf(Id<Food> food, Instant day);

    Single<List<FoodEventFeedItem>> getFoodEventsOf(Id<Food> food, Instant day);

    Single<List<FoodItemEventFeedItem>> getFoodItemEventsOf(Id<Food> food, Instant day);

    Maybe<Instant> getPreviousDayContainingEvents(Instant day);

    Maybe<Instant> getNextDayContainingEvents(Instant day);
}
