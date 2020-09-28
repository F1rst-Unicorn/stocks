/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.android.dagger.modules;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.njsm.stocks.android.contentprovider.RecentSearchSuggestionsProvider;
import de.njsm.stocks.android.frontend.additem.AddItemFragment;
import de.njsm.stocks.android.frontend.additem.EditItemFragment;
import de.njsm.stocks.android.frontend.allfood.AllFoodFragment;
import de.njsm.stocks.android.frontend.crashlog.CrashLogListFragment;
import de.njsm.stocks.android.frontend.device.DeviceFragment;
import de.njsm.stocks.android.frontend.eannumber.EanNumberFragment;
import de.njsm.stocks.android.frontend.emptyfood.EmptyFoodFragment;
import de.njsm.stocks.android.frontend.food.FoodFragment;
import de.njsm.stocks.android.frontend.foodcharts.FoodChartFragment;
import de.njsm.stocks.android.frontend.foodhistory.FoodHistoryFragment;
import de.njsm.stocks.android.frontend.fooditem.FoodItemFragment;
import de.njsm.stocks.android.frontend.locationhistory.LocationHistoryFragment;
import de.njsm.stocks.android.frontend.locations.LocationFragment;
import de.njsm.stocks.android.frontend.main.MainActivity;
import de.njsm.stocks.android.frontend.main.OutlineFragment;
import de.njsm.stocks.android.frontend.search.SearchFragment;
import de.njsm.stocks.android.frontend.settings.SettingsFragment;
import de.njsm.stocks.android.frontend.shoppinglist.ShoppingListFragment;
import de.njsm.stocks.android.frontend.startup.StartupFragment;
import de.njsm.stocks.android.frontend.user.UserFragment;

@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract UserFragment contributeUserFragment();

    @ContributesAndroidInjector
    abstract EmptyFoodFragment contributeEmptyFoodFragment();

    @ContributesAndroidInjector
    abstract FoodFragment contributeFoodFragment();

    @ContributesAndroidInjector
    abstract FoodItemFragment contributeFoodItemFragment();

    @ContributesAndroidInjector
    abstract AddItemFragment contributeAddItemFragment();

    @ContributesAndroidInjector
    abstract EditItemFragment contributeEditItemFragment();

    @ContributesAndroidInjector
    abstract EanNumberFragment contributeEanNumberFragment();

    @ContributesAndroidInjector
    abstract LocationFragment contributeLocationFragment();

    @ContributesAndroidInjector
    abstract DeviceFragment contributeUserDeviceFragment();

    @ContributesAndroidInjector
    abstract OutlineFragment contributeOutlineFragment();

    @ContributesAndroidInjector
    abstract SearchFragment contributeSearchFragment();

    @ContributesAndroidInjector
    abstract AllFoodFragment contributeAllFoodFragment();

    @ContributesAndroidInjector
    abstract StartupFragment contributeStartupFragment();

    @ContributesAndroidInjector
    abstract SettingsFragment contributeSettingsFragment();

    @ContributesAndroidInjector
    abstract CrashLogListFragment contributeCrashLogFragment();

    @ContributesAndroidInjector
    abstract ShoppingListFragment contributeShoppingListFragment();

    @ContributesAndroidInjector
    abstract FoodHistoryFragment contributeFoodHistoryFragment();

    @ContributesAndroidInjector
    abstract LocationHistoryFragment contributeLocationHistoryFragment();

    @ContributesAndroidInjector
    abstract FoodChartFragment contributeFoodChartFragment();

    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector
    abstract RecentSearchSuggestionsProvider contributeRecentSearchSuggestionsProvider();
}
