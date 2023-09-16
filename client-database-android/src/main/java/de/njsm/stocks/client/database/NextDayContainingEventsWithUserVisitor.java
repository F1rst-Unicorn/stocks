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
import de.njsm.stocks.client.business.entities.User;
import io.reactivex.rxjava3.core.Maybe;

import java.time.Instant;
import java.util.List;

public class NextDayContainingEventsWithUserVisitor extends NextDayContainingEventsVisitor {

    private final IdImpl<User> id;

    static NextDayContainingEventsVisitor intoFuture(EventDao eventDao, List<EntityType> queriedEntities, Instant day, IdImpl<User> id) {
        return new NextDayContainingEventsWithUserVisitor(eventDao, queriedEntities, day, false, id);
    }

    static NextDayContainingEventsVisitor intoPast(EventDao eventDao, List<EntityType> queriedEntities, Instant day, IdImpl<User> id) {
        return new NextDayContainingEventsWithUserVisitor(eventDao, queriedEntities, day, true, id);
    }

    public NextDayContainingEventsWithUserVisitor(EventDao eventDao, List<EntityType> relevantEntities, Instant day, boolean previous, IdImpl<User> id) {
        super(eventDao, relevantEntities, day, previous);
        this.id = id;
    }

    @Override
    public Maybe<Instant> location(Void input) {
        return eventDao.getNextDayContainingLocationEventsOfInitiatorUser(day, previous, id.id());
    }

    @Override
    public Maybe<Instant> user(Void input) {
        return eventDao.getNextDayContainingUserEventsOfInitiatorUser(day, previous, id.id());
    }

    @Override
    public Maybe<Instant> userDevice(Void input) {
        return eventDao.getNextDayContainingUserDeviceEventsOfInitiatorUser(day, previous, id.id());
    }

    @Override
    public Maybe<Instant> food(Void input) {
        return eventDao.getNextDayContainingFoodEventsOfInitiatorUser(day, previous, id.id());
    }

    @Override
    public Maybe<Instant> eanNumber(Void input) {
        return eventDao.getNextDayContainingEanNumberEventsUser(day, previous, id.id());
    }

    @Override
    public Maybe<Instant> foodItem(Void input) {
        return eventDao.getNextDayContainingFoodItemEventsOfInitiatorUser(day, previous, id.id());
    }

    @Override
    public Maybe<Instant> unit(Void input) {
        return eventDao.getNextDayContainingUnitEventsOfInitiatorUser(day, previous, id.id());
    }

    @Override
    public Maybe<Instant> scaledUnit(Void input) {
        return eventDao.getNextDayContainingScaledUnitEventsOfInitiatorUser(day, previous, id.id());
    }
}
