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

import de.njsm.stocks.client.fragment.errorlist.ErrorListFragmentDirections;

import javax.inject.Inject;

class ErrorListNavigatorImpl extends BaseNavigator implements ErrorListNavigator {

    @Inject
    ErrorListNavigatorImpl(NavigationArgConsumer navigationArgConsumer) {
        super(navigationArgConsumer);
    }

    @Override
    public void showErrorDetails(long id) {
        getNavigationArgConsumer().navigate(ErrorListFragmentDirections.actionNavFragmentErrorListToNavFragmentErrorDetail(id));
    }

    @Override
    public void resolveLocationEditConflict(long id) {
        getNavigationArgConsumer().navigate(ErrorListFragmentDirections.actionNavFragmentErrorListToNavFragmentLocationConflict(id));
    }

    @Override
    public void resolveUnitEditConflict(long id) {
        getNavigationArgConsumer().navigate(ErrorListFragmentDirections.actionNavFragmentErrorListToNavFragmentUnitConflict(id));
    }

    @Override
    public void resolveScaledUnitEditConflict(long id) {
        getNavigationArgConsumer().navigate(ErrorListFragmentDirections.actionNavFragmentErrorListToNavFragmentScaledUnitConflict(id));
    }

    @Override
    public void resolveFoodEditConflict(long id) {
        getNavigationArgConsumer().navigate(ErrorListFragmentDirections.actionNavFragmentErrorListToNavFragmentFoodConflict(id));
    }

    @Override
    public void resolveFoodItemEditConflict(long id) {
        getNavigationArgConsumer().navigate(ErrorListFragmentDirections.actionNavFragmentErrorListToNavFragmentFoodItemConflict(id));
    }
}
