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
import de.njsm.stocks.client.fragment.searchedfood.SearchedFoodFragmentArgs;
import de.njsm.stocks.client.fragment.searchedfood.SearchedFoodFragmentDirections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

class SearchedFoodNavigatorImpl extends BaseNavigator implements SearchedFoodNavigator {

    private static final Logger LOG = LoggerFactory.getLogger(LocationListNavigatorImpl.class);

    @Inject
    SearchedFoodNavigatorImpl(NavigationArgConsumer navigationArgConsumer) {
        super(navigationArgConsumer);
    }

    @Override
    public void addFood() {
        LOG.info("Button is hidden, so nothing happens");
    }

    @Override
    public void showFood(int id) {
        var direction = SearchedFoodFragmentDirections.actionNavFragmentSearchResultsToNavFragmentFoodItemTabs(id);
        getNavigationArgConsumer().navigate(direction);
    }

    @Override
    public void editFood(int id) {
        var direction = SearchedFoodFragmentDirections.actionNavFragmentSearchResultsToNavFragmentEditFood(id);
        getNavigationArgConsumer().navigate(direction);
    }

    @Override
    public String getQuery(Bundle arguments) {
        return SearchedFoodFragmentArgs.fromBundle(arguments).getQuery();
    }
}
