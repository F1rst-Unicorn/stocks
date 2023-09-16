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
import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.client.business.entities.Location;
import io.reactivex.rxjava3.core.Maybe;

import java.time.Instant;
import java.util.List;

public class NextDayContainingEventsWithLocationVisitor extends NextDayContainingEventsVisitor {

    private final IdImpl<Location> location;

    static NextDayContainingEventsVisitor intoFuture(EventDao eventDao, List<EntityType> queriedEntities, Instant day, IdImpl<Location> location) {
        return new NextDayContainingEventsWithLocationVisitor(eventDao, queriedEntities, day, false, location);
    }

    static NextDayContainingEventsVisitor intoPast(EventDao eventDao, List<EntityType> queriedEntities, Instant day, IdImpl<Location> location) {
        return new NextDayContainingEventsWithLocationVisitor(eventDao, queriedEntities, day, true, location);
    }

    NextDayContainingEventsWithLocationVisitor(EventDao eventDao, List<EntityType> queriedEntities, Instant day, boolean previous, IdImpl<Location> location) {
        super(eventDao, queriedEntities, day, previous);
        this.location = location;
    }

    @Override
    public Maybe<Instant> location(Void input) {
        return eventDao.getNextDayContainingLocationEvents(day, location.id(), previous);
    }

    @Override
    public Maybe<Instant> foodItem(Void input) {
        return eventDao.getNextDayContainingFoodItemEventsOfLocation(day, location.id(), previous);
    }
}
