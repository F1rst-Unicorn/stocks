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

import de.njsm.stocks.client.business.Localiser;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.event.ActivityEvent;
import io.reactivex.rxjava3.core.Single;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;

public class FoodEventInteractorImpl extends BaseEventInteractorImpl implements EventInteractor {

    private final Id<Food> food;

    @Inject
    public FoodEventInteractorImpl(Id<Food> food, EventRepository repository, ActivityEventFactory eventFactory, Localiser localiser) {
        super(repository, eventFactory, localiser);
        this.food = food;
    }

    @Override
    public Single<List<ActivityEvent>> getEventsOf(LocalDate day) {
        return repository.getFoodEventsOf(food, localiser.toInstant(day))
                        .map(v -> transformToEvents(v, eventFactory::getFoodEventFrom))
                .mergeWith(repository.getFoodItemEventsOf(food, localiser.toInstant(day))
                        .map(v -> transformToEvents(v, eventFactory::getFoodItemEventFrom)))
                .mergeWith(repository.getEanNumberEventsOf(food, localiser.toInstant(day))
                        .map(v -> transformToEvents(v, eventFactory::getEanNumberEventFrom)))

                .buffer(3) // align with number of merged feeds above
                .map(this::sortEvents)
                .first(emptyList());
    }
}
