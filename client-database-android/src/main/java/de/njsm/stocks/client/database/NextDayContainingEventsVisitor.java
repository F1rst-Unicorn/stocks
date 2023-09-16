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

import com.google.common.collect.Comparators;
import de.njsm.stocks.client.business.entities.EntityType;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.functions.BiFunction;

import java.time.Instant;
import java.util.List;

class NextDayContainingEventsVisitor implements EntityType.Visitor<Void, Maybe<Instant>> {

    final EventDao eventDao;

    private final List<EntityType> queriedEntities;

    final Instant day;

    final boolean previous;

    static NextDayContainingEventsVisitor intoFuture(EventDao eventDao, List<EntityType> queriedEntities, Instant day) {
        return new NextDayContainingEventsVisitor(eventDao, queriedEntities, day, false);
    }

    static NextDayContainingEventsVisitor intoPast(EventDao eventDao, List<EntityType> queriedEntities, Instant day) {
        return new NextDayContainingEventsVisitor(eventDao, queriedEntities, day, true);
    }

    NextDayContainingEventsVisitor(EventDao eventDao, List<EntityType> queriedEntities, Instant day, boolean previous) {
        this.eventDao = eventDao;
        this.queriedEntities = queriedEntities;
        this.day = day;
        this.previous = previous;
    }

    Maybe<Instant> getNextDayContainingEvents() {
        BiFunction<Instant, Instant, Instant> reducer;
        if (previous) {
            reducer = Comparators::max;
        } else {
            reducer = Comparators::min;
        }
        return queriedEntities.stream()
                .map(v -> visit(v, null))
                .map(Maybe::toFlowable)
                .reduce(Flowable.empty(), Flowable::concat)
                .reduce(reducer);
    }

    @Override
    public Maybe<Instant> location(Void input) {
        return eventDao.getNextDayContainingLocationEvents(day, previous);
    }

    @Override
    public Maybe<Instant> user(Void input) {
        return eventDao.getNextDayContainingUserEvents(day, previous);
    }

    @Override
    public Maybe<Instant> userDevice(Void input) {
        return eventDao.getNextDayContainingUserDeviceEvents(day, previous);
    }

    @Override
    public Maybe<Instant> food(Void input) {
        return eventDao.getNextDayContainingFoodEvents(day, previous);
    }

    @Override
    public Maybe<Instant> eanNumber(Void input) {
        return eventDao.getNextDayContainingEanNumberEvents(day, previous);
    }

    @Override
    public Maybe<Instant> foodItem(Void input) {
        return eventDao.getNextDayContainingFoodItemEvents(day, previous);
    }

    @Override
    public Maybe<Instant> unit(Void input) {
        return eventDao.getNextDayContainingUnitEvents(day, previous);
    }

    @Override
    public Maybe<Instant> scaledUnit(Void input) {
        return eventDao.getNextDayContainingScaledUnitEvents(day, previous);
    }

    @Override
    public Maybe<Instant> recipe(Void input) {
        return eventDao.getNextDayContainingRecipeEvents(day, previous);
    }

    @Override
    public Maybe<Instant> recipeIngredient(Void input) {
        return eventDao.getNextDayContainingRecipeIngredientEvents(day, previous);
    }

    @Override
    public Maybe<Instant> recipeProduct(Void input) {
        return eventDao.getNextDayContainingRecipeProductEvents(day, previous);
    }
}
