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

package de.njsm.stocks.client.navigation;

import android.os.Bundle;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.fragment.history.HistoryFragmentArgs;

import javax.inject.Inject;
import java.util.Optional;

class HistoryNavigatorImpl extends BaseNavigator implements HistoryNavigator {

    @Inject
    HistoryNavigatorImpl(NavigationArgConsumer navigationArgConsumer) {
        super(navigationArgConsumer);
    }

    @Override
    public Optional<Id<Food>> getFood(Bundle arguments) {
        var args = HistoryFragmentArgs.fromBundle(arguments);
        return Optional.of(args.getFoodId())
                .filter(v -> v != -1)
                .map(IdImpl::create);
    }

    @Override
    public Optional<Id<Location>> getLocation(Bundle arguments) {
        var args = HistoryFragmentArgs.fromBundle(arguments);
        return Optional.of(args.getLocationId())
                .filter(v -> v != -1)
                .map(IdImpl::create);
    }

    @Override
    public Optional<Id<User>> getUser(Bundle arguments) {
        var args = HistoryFragmentArgs.fromBundle(arguments);
        return Optional.of(args.getUserId())
                .filter(v -> v != -1)
                .map(IdImpl::create);
    }

    @Override
    public Optional<Id<UserDevice>> getUserDevice(Bundle arguments) {
        var args = HistoryFragmentArgs.fromBundle(arguments);
        return Optional.of(args.getUserDeviceId())
                .filter(v -> v != -1)
                .map(IdImpl::create);
    }
}
