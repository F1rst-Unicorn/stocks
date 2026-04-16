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
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.client.fragment.fooditemtabs.FoodItemTabsFragmentDirections;
import de.njsm.stocks.client.fragment.priceshow.PriceShowFragmentArgs;

import javax.inject.Inject;

public class PriceShowNavigatorImpl extends BaseNavigator implements PriceShowNavigator {

    @Inject
    PriceShowNavigatorImpl(NavigationArgConsumer navigationArgConsumer) {
        super(navigationArgConsumer);
    }

    @Override
    public IdImpl<Food> getFoodId(Bundle args) {
        return IdImpl.create(PriceShowFragmentArgs.fromBundle(args).getFoodId());
    }

    @Override
    public void editFood(Id<Food> foodId) {
        var direction = FoodItemTabsFragmentDirections.actionNavFragmentFoodItemTabsToNavFragmentEditFood(foodId.id());
        getNavigationArgConsumer().navigate(direction);
    }

    @Override
    public void showEanNumbers(Id<Food> foodId) {
        var direction = FoodItemTabsFragmentDirections.actionNavFragmentFoodItemTabsToNavFragmentEanNumbers(foodId.id());
        getNavigationArgConsumer().navigate(direction);
    }

    @Override
    public void showHistory(Id<Food> foodId) {
        getNavigationArgConsumer().navigate(
                FoodItemTabsFragmentDirections.actionNavFragmentFoodItemTabsToNavFragmentHistory(foodId.id())
        );
    }

    @Override
    public void addPrice(Id<Food> foodId) {
        getNavigationArgConsumer().navigate(
                FoodItemTabsFragmentDirections.actionNavFragmentFoodItemTabsToNavFragmentPriceAdd(foodId.id())
        );
    }
}
