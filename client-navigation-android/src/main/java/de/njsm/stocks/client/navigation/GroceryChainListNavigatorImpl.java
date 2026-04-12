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

import de.njsm.stocks.client.fragment.grocerychainlist.GroceryChainListFragmentDirections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

class GroceryChainListNavigatorImpl extends BaseNavigator implements GroceryChainListNavigator {

    private static final Logger LOG = LoggerFactory.getLogger(GroceryChainListNavigatorImpl.class);

    @Inject
    GroceryChainListNavigatorImpl(NavigationArgConsumer navigationArgConsumer) {
        super(navigationArgConsumer);
    }

    @Override
    public void addGroceryChain() {
        LOG.debug("adding a grocery chain");
        getNavigationArgConsumer().navigate(
                GroceryChainListFragmentDirections.actionNavFragmentGroceryChainListToNavFragmentGroceryChainAdd()
        );
    }

    @Override
    public void showGroceryChain(int id) {
        LOG.debug("showing grocery chain " + id);
        getNavigationArgConsumer().navigate(
                GroceryChainListFragmentDirections.actionNavFragmentGroceryChainListToNavFragmentGroceryChainContent(id)
        );
    }

    @Override
    public void editGroceryChain(int id) {
        LOG.debug("editing grocery chain " + id);
        getNavigationArgConsumer().navigate(
                GroceryChainListFragmentDirections.actionNavFragmentGroceryChainListToNavFragmentGroceryChainEdit(id)
        );
    }
}
