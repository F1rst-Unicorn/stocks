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

import de.njsm.stocks.client.business.entities.*;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

import java.time.Instant;
import java.util.List;

public interface EventRepository {

    Observable<Instant> getNewEventNotifier(List<EntityType> relevantEntities);

    Single<List<LocationEventFeedItem>> getLocationFeed(EventKeyHint hint, Instant day);

    Single<List<UnitEventFeedItem>> getUnitFeed(EventKeyHint hint, Instant day);

    Single<List<UserEventFeedItem>> getUserFeed(EventKeyHint hint, Instant day);

    Single<List<UserDeviceEventFeedItem>> getUserDeviceFeed(EventKeyHint hint, Instant day);

    Single<List<ScaledUnitEventFeedItem>> getScaledUnitFeed(EventKeyHint hint, Instant day);

    Single<List<FoodEventFeedItem>> getFoodFeed(EventKeyHint hint, Instant day);

    Single<List<FoodItemEventFeedItem>> getFoodItemFeed(EventKeyHint hint, Instant day);

    Single<List<EanNumberEventFeedItem>> getEanNumberFeed(EventKeyHint hint, Instant day);

    Maybe<Instant> getPreviousDayContainingEvents(Instant day, List<EntityType> relevantEntities, EventKeyHint hint);

    Maybe<Instant> getNextDayContainingEvents(Instant day, List<EntityType> relevantEntities, EventKeyHint hint);
}
