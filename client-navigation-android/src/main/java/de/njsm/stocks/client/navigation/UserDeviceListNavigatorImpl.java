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
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.User;
import de.njsm.stocks.client.business.entities.UserDevice;
import de.njsm.stocks.client.fragment.userdevicelist.UserDeviceListFragmentArgs;
import de.njsm.stocks.client.fragment.userdevicelist.UserDeviceListFragmentDirections;

import javax.inject.Inject;

class UserDeviceListNavigatorImpl extends BaseNavigator implements UserDeviceListNavigator {

    @Inject
    UserDeviceListNavigatorImpl(NavigationArgConsumer navigationArgConsumer) {
        super(navigationArgConsumer);
    }

    @Override
    public void add(Id<User> userId) {
        var direction = UserDeviceListFragmentDirections.actionNavFragmentDeviceListToNavFragmentUserDeviceAdd(userId.id());
        getNavigationArgConsumer().navigate(direction);
    }

    @Override
    public Id<User> getUserId(Bundle arguments) {
        return UserDeviceListFragmentArgs.fromBundle(arguments)::getId;
    }

    @Override
    public void showTicket(Id<UserDevice> id) {
        var direction = UserDeviceListFragmentDirections.actionNavFragmentDeviceListToNavFragmentShowTicket(id.id());
        getNavigationArgConsumer().navigate(direction);
    }

    @Override
    public void showHistory(Id<User> id) {
        var direction = UserDeviceListFragmentDirections.actionNavFragmentDeviceListToNavFragmentUserHistory(id.id());
        getNavigationArgConsumer().navigate(direction);
    }

    @Override
    public void showDeviceHistory(Id<UserDevice> id) {
        var direction = UserDeviceListFragmentDirections.actionNavFragmentDeviceListToNavFragmentUserDeviceHistory(id.id());
        getNavigationArgConsumer().navigate(direction);
    }
}
