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
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.execution.Scheduler;

import javax.inject.Inject;

public class EventInteractorFactoryImpl implements EventInteractorFactory {

    private final EventRepository repository;

    private final ActivityEventFactory eventFactory;

    private final Localiser localiser;

    private final Scheduler scheduler;

    @Inject
    EventInteractorFactoryImpl(EventRepository repository, ActivityEventFactory eventFactory, Localiser localiser, Scheduler scheduler) {
        this.repository = repository;
        this.eventFactory = eventFactory;
        this.localiser = localiser;
        this.scheduler = scheduler;
    }

    @Override
    public EventInteractor forLocation(Id<Location> location) {
        return new LocationEventInteractorImpl(location, repository, eventFactory, localiser, scheduler);
    }

    @Override
    public EventInteractor forFood(Id<Food> food) {
        return new FoodEventInteractorImpl(food, repository, eventFactory, localiser, scheduler);
    }

    @Override
    public EventInteractor forUser(Id<User> user) {
        return new UserEventInteractorImpl(user, repository, eventFactory, localiser, scheduler);
    }

    @Override
    public EventInteractor forUserDevice(Id<UserDevice> userDevice) {
        return new UserDeviceEventInteractorImpl(userDevice, repository, eventFactory, localiser, scheduler);
    }
}
