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
import de.njsm.stocks.client.business.entities.GroceryChain;
import de.njsm.stocks.client.business.entities.GroceryStore;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.client.fragment.grocerystorelist.GroceryStoreListFragmentArgs;
import de.njsm.stocks.client.fragment.grocerystorelist.GroceryStoreListFragmentDirections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

class GroceryStoreListNavigatorImpl extends BaseNavigator implements GroceryStoreListNavigator {

    private static final Logger LOG = LoggerFactory.getLogger(GroceryStoreListNavigatorImpl.class);

    @Inject
    GroceryStoreListNavigatorImpl(NavigationArgConsumer navigationArgConsumer) {
        super(navigationArgConsumer);
    }

    @Override
    public void addGroceryStore(Id<GroceryChain> id) {
        LOG.debug("adding a grocery store");
        getNavigationArgConsumer().navigate(
                GroceryStoreListFragmentDirections.actionNavFragmentGroceryStoreListToNavFragmentGroceryStoreAdd(id.id())
        );
    }

    @Override
    public void editGroceryStore(Id<GroceryStore> id) {
        LOG.debug("editing grocery store " + id);
        getNavigationArgConsumer().navigate(
                GroceryStoreListFragmentDirections.actionNavFragmentGroceryStoreListToNavFragmentGroceryStoreEdit(id.id())
        );
    }

    @Override
    public IdImpl<GroceryChain> getGroceryChain(Bundle bundle) {
        return IdImpl.create(GroceryStoreListFragmentArgs.fromBundle(bundle).getId());
    }
}
