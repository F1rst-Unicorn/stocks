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
import io.reactivex.rxjava3.core.Maybe;

import java.time.Instant;
import java.util.List;

class NextDayContainingEventsVisitor implements EntityType.Visitor<Void, Maybe<Instant>> {

    final EventDao eventDao;

    private final List<EntityType> queriedEntities;

    final Instant day;

    NextDayContainingEventsVisitor(EventDao eventDao, List<EntityType> queriedEntities, Instant day) {
        this.eventDao = eventDao;
        this.queriedEntities = queriedEntities;
        this.day = day;
    }

    Maybe<Instant> getNextDayContainingEvents() {
        return queriedEntities.stream()
                .map(v -> visit(v, null))
                .reduce(Maybe.just(Instant.MAX), (a, b) -> Maybe.zip(a, b, Comparators::min));
    }

    @Override
    public Maybe<Instant> location(Void input) {
        return eventDao.getNextDayContainingLocationEvents(day);
    }

    @Override
    public Maybe<Instant> user(Void input) {
        return eventDao.getNextDayContainingUserEvents(day);
    }

    @Override
    public Maybe<Instant> userDevice(Void input) {
        return eventDao.getNextDayContainingUserDeviceEvents(day);
    }

    @Override
    public Maybe<Instant> food(Void input) {
        return eventDao.getNextDayContainingFoodEvents(day);
    }

    @Override
    public Maybe<Instant> eanNumber(Void input) {
        return eventDao.getNextDayContainingEanNumberEvents(day);
    }

    @Override
    public Maybe<Instant> foodItem(Void input) {
        return eventDao.getNextDayContainingFoodItemEvents(day);
    }

    @Override
    public Maybe<Instant> unit(Void input) {
        return eventDao.getNextDayContainingUnitEvents(day);
    }

    @Override
    public Maybe<Instant> scaledUnit(Void input) {
        return eventDao.getNextDayContainingScaledUnitEvents(day);
    }

    @Override
    public Maybe<Instant> recipe(Void input) {
        return eventDao.getNextDayContainingRecipeEvents(day);
    }

    @Override
    public Maybe<Instant> recipeIngredient(Void input) {
        return eventDao.getNextDayContainingRecipeIngredientEvents(day);
    }

    @Override
    public Maybe<Instant> recipeProduct(Void input) {
        return eventDao.getNextDayContainingRecipeProductEvents(day);
    }
}
