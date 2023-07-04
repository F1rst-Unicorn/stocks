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

import java.time.Instant;
import java.util.List;

class PreviousDayContainingEventsVisitor implements EntityType.Visitor<Void, Maybe<Instant>> {

    final EventDao eventDao;

    private final List<EntityType> queriedEntities;

    final Instant day;

    PreviousDayContainingEventsVisitor(EventDao eventDao, List<EntityType> queriedEntities, Instant day) {
        this.eventDao = eventDao;
        this.queriedEntities = queriedEntities;
        this.day = day;
    }

    Maybe<Instant> getPreviousDayContainingEvents() {
        return queriedEntities.stream()
                .map(v -> visit(v, null))
                .map(Maybe::toFlowable)
                .reduce(Flowable.empty(), Flowable::concat)
                .reduce(Comparators::max);
    }

    @Override
    public Maybe<Instant> location(Void input) {
        return eventDao.getPreviousDayContainingLocationEvents(day);
    }

    @Override
    public Maybe<Instant> user(Void input) {
        return eventDao.getPreviousDayContainingUserEvents(day);
    }

    @Override
    public Maybe<Instant> userDevice(Void input) {
        return eventDao.getPreviousDayContainingUserDeviceEvents(day);
    }

    @Override
    public Maybe<Instant> food(Void input) {
        return eventDao.getPreviousDayContainingFoodEvents(day);
    }

    @Override
    public Maybe<Instant> eanNumber(Void input) {
        return eventDao.getPreviousDayContainingEanNumberEvents(day);
    }

    @Override
    public Maybe<Instant> foodItem(Void input) {
        return eventDao.getPreviousDayContainingFoodItemEvents(day);
    }

    @Override
    public Maybe<Instant> unit(Void input) {
        return eventDao.getPreviousDayContainingUnitEvents(day);
    }

    @Override
    public Maybe<Instant> scaledUnit(Void input) {
        return eventDao.getPreviousDayContainingScaledUnitEvents(day);
    }

    @Override
    public Maybe<Instant> recipe(Void input) {
        return eventDao.getPreviousDayContainingRecipeEvents(day);
    }

    @Override
    public Maybe<Instant> recipeIngredient(Void input) {
        return eventDao.getPreviousDayContainingRecipeIngredientEvents(day);
    }

    @Override
    public Maybe<Instant> recipeProduct(Void input) {
        return eventDao.getPreviousDayContainingRecipeProductEvents(day);
    }
}
