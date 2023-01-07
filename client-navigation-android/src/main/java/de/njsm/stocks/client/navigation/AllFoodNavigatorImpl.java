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

import androidx.navigation.NavDirections;
import de.njsm.stocks.client.fragment.allfood.AllFoodFragmentDirections;

import javax.inject.Inject;


class AllFoodNavigatorImpl extends BaseNavigator implements AllFoodNavigator {

    @Inject
    AllFoodNavigatorImpl(NavigationArgConsumer navigationArgConsumer) {
        super(navigationArgConsumer);
    }

    @Override
    public void addFood() {
        NavDirections direction = AllFoodFragmentDirections.actionNavFragmentAllFoodToNavFragmentAddFood();
        getNavigationArgConsumer().navigate(direction);
    }

    @Override
    public void showFood(int id) {
        NavDirections direction = AllFoodFragmentDirections.actionNavFragmentAllFoodToNavFragmentFoodItemTabs(id);
        getNavigationArgConsumer().navigate(direction);
    }

    @Override
    public void editFood(int id) {
        NavDirections direction = AllFoodFragmentDirections.actionNavFragmentAllFoodToNavFragmentEditFood(id);
        getNavigationArgConsumer().navigate(direction);
    }
}
