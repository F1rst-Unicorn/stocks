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

import de.njsm.stocks.client.fragment.unittabs.UnitTabsFragmentDirections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

class ScaledUnitListNavigatorImpl extends NavigatorImpl implements ScaledUnitListNavigator {

    private static final Logger LOG = LoggerFactory.getLogger(ScaledUnitListNavigatorImpl.class);

    @Inject
    ScaledUnitListNavigatorImpl(NavigationArgConsumer navigationArgConsumer) {
        super(navigationArgConsumer);
    }

    @Override
    public void addScaledUnit() {
        LOG.debug("adding a scaled unit");
        getNavigationArgConsumer().navigate(
                UnitTabsFragmentDirections.actionNavFragmentUnitTabsToNavFragmentScaledUnitAdd()
        );
    }

    @Override
    public void editScaledUnit(int id) {
        LOG.debug("editing scaled unit " + id);
        getNavigationArgConsumer().navigate(
                UnitTabsFragmentDirections.actionNavFragmentUnitTabsToNavFragmentScaledUnitEdit(id)
        );
    }
}
