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
import de.njsm.stocks.client.business.entities.IdImpl;
import io.reactivex.rxjava3.core.Maybe;

import java.time.Instant;
import java.util.List;

public class NextDayContainingEventsWithFoodVisitor extends NextDayContainingEventsVisitor {

    private final IdImpl<Food> food;

    NextDayContainingEventsWithFoodVisitor(EventDao eventDao, List<EntityType> queriedEntities, Instant day, IdImpl<Food> food) {
        super(eventDao, queriedEntities, day);
        this.food = food;
    }

    @Override
    public Maybe<Instant> food(Void input) {
        return eventDao.getNextDayContainingFoodEvents(day, food.id());
    }

    @Override
    public Maybe<Instant> foodItem(Void input) {
        return eventDao.getNextDayContainingFoodItemEventsOfFood(day, food.id());
    }

    @Override
    public Maybe<Instant> eanNumber(Void input) {
        return eventDao.getNextDayContainingEanNumberEvents(day, food.id());
    }
}
