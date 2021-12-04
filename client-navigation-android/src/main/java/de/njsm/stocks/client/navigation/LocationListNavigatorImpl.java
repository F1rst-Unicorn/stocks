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

import de.njsm.stocks.client.view.LocationListFragmentDirections;

import javax.inject.Inject;

public class LocationListNavigatorImpl implements LocationListNavigator {

    private final NavigationArgConsumer navigationArgConsumer;

    @Inject
    public LocationListNavigatorImpl(NavigationArgConsumer navigationArgConsumer) {
        this.navigationArgConsumer = navigationArgConsumer;
    }

    @Override
    public void addLocation() {
        navigationArgConsumer.navigate(
                LocationListFragmentDirections.actionNavFragmentLocationListToNavFragmentLocationAdd()
        );
    }

    @Override
    public void showLocation(int id) {
        navigationArgConsumer.navigate(
                LocationListFragmentDirections.actionNavFragmentLocationListToNavFragmentLocationContent(id)
        );
    }

    @Override
    public void editLocation(int id) {
        navigationArgConsumer.navigate(
                LocationListFragmentDirections.actionNavFragmentLocationListToNavFragmentLocationEdit(id)
        );
    }
}
