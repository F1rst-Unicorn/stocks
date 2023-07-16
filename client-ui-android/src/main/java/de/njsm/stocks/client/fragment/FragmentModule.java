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
 */

package de.njsm.stocks.client.fragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.njsm.stocks.client.fragment.allfood.AllFoodFragment;
import de.njsm.stocks.client.fragment.crashlist.CrashLogListFragment;
import de.njsm.stocks.client.fragment.eanassign.FoodEanNumberAssignmentFragment;
import de.njsm.stocks.client.fragment.eanlist.EanNumberListFragment;
import de.njsm.stocks.client.fragment.emptyfood.EmptyFoodFragment;
import de.njsm.stocks.client.fragment.errordetails.ErrorDetailsFragment;
import de.njsm.stocks.client.fragment.errorlist.ErrorListFragment;
import de.njsm.stocks.client.fragment.foodadd.FoodAddFragment;
import de.njsm.stocks.client.fragment.foodconflict.FoodConflictFragment;
import de.njsm.stocks.client.fragment.fooddetails.FoodDetailsFragment;
import de.njsm.stocks.client.fragment.foodedit.FoodEditFragment;
import de.njsm.stocks.client.fragment.foodinlocation.FoodInLocationFragment;
import de.njsm.stocks.client.fragment.fooditemadd.FoodItemAddFragment;
import de.njsm.stocks.client.fragment.fooditemconflict.FoodItemConflictFragment;
import de.njsm.stocks.client.fragment.fooditemedit.FoodItemEditFragment;
import de.njsm.stocks.client.fragment.fooditemlist.FoodItemListFragment;
import de.njsm.stocks.client.fragment.fooditemtabs.FoodItemTabsFragment;
import de.njsm.stocks.client.fragment.history.HistoryFragment;
import de.njsm.stocks.client.fragment.locationadd.LocationAddFragment;
import de.njsm.stocks.client.fragment.locationconflict.LocationConflictFragment;
import de.njsm.stocks.client.fragment.locationedit.LocationEditFragment;
import de.njsm.stocks.client.fragment.locationlist.LocationListFragment;
import de.njsm.stocks.client.fragment.outline.OutlineFragment;
import de.njsm.stocks.client.fragment.recipeadd.RecipeAddFragment;
import de.njsm.stocks.client.fragment.recipecook.RecipeCookFragment;
import de.njsm.stocks.client.fragment.recipedetail.RecipeDetailFragment;
import de.njsm.stocks.client.fragment.recipeedit.RecipeEditFragment;
import de.njsm.stocks.client.fragment.recipelist.RecipeListFragment;
import de.njsm.stocks.client.fragment.scaledunitadd.ScaledUnitAddFragment;
import de.njsm.stocks.client.fragment.scaledunitconflict.ScaledUnitConflictFragment;
import de.njsm.stocks.client.fragment.scaledunitedit.ScaledUnitEditFragment;
import de.njsm.stocks.client.fragment.scaledunitlist.ScaledUnitListFragment;
import de.njsm.stocks.client.fragment.searchedfood.SearchedFoodFragment;
import de.njsm.stocks.client.fragment.settings.SettingsFragment;
import de.njsm.stocks.client.fragment.setupform.SetupFormFragment;
import de.njsm.stocks.client.fragment.setupgreet.SetupGreetingFragment;
import de.njsm.stocks.client.fragment.shoppinglist.ShoppingListFragment;
import de.njsm.stocks.client.fragment.ticketshow.TicketShowFragment;
import de.njsm.stocks.client.fragment.unitadd.UnitAddFragment;
import de.njsm.stocks.client.fragment.unitconflict.UnitConflictFragment;
import de.njsm.stocks.client.fragment.unitedit.UnitEditFragment;
import de.njsm.stocks.client.fragment.unitlist.UnitListFragment;
import de.njsm.stocks.client.fragment.unittabs.UnitTabsFragment;
import de.njsm.stocks.client.fragment.useradd.UserAddFragment;
import de.njsm.stocks.client.fragment.userdeviceadd.UserDeviceAddFragment;
import de.njsm.stocks.client.fragment.userdevicelist.UserDeviceListFragment;
import de.njsm.stocks.client.fragment.userlist.UserListFragment;

@Module
public abstract class FragmentModule {

    @ContributesAndroidInjector
    public abstract LocationListFragment locationListFragment();

    @ContributesAndroidInjector
    public abstract LocationAddFragment locationAddFragment();

    @ContributesAndroidInjector
    public abstract SetupGreetingFragment setupGreetingFragment();

    @ContributesAndroidInjector
    public abstract SetupFormFragment setupFormFragment();

    @ContributesAndroidInjector
    public abstract BottomToolbarFragment bottomToolbarFragment();

    @ContributesAndroidInjector
    public abstract ErrorListFragment errorListFragment();

    @ContributesAndroidInjector
    public abstract ErrorDetailsFragment errorDetailsFragment();

    @ContributesAndroidInjector
    public abstract LocationEditFragment locationEditFragment();

    @ContributesAndroidInjector
    public abstract LocationConflictFragment locationConflictFragment();

    @ContributesAndroidInjector
    public abstract UnitListFragment unitListFragment();

    @ContributesAndroidInjector
    public abstract UnitTabsFragment UnitTabsFragment();

    @ContributesAndroidInjector
    public abstract ScaledUnitListFragment ScaledUnitListFragment();

    @ContributesAndroidInjector
    public abstract UnitAddFragment UnitAddFragment();

    @ContributesAndroidInjector
    public abstract UnitEditFragment UnitEditFragment();

    @ContributesAndroidInjector
    public abstract UnitConflictFragment UnitConflictFragment();

    @ContributesAndroidInjector
    public abstract ScaledUnitAddFragment ScaledUnitAddFragment();

    @ContributesAndroidInjector
    public abstract ScaledUnitEditFragment ScaledUnitEditFragment();

    @ContributesAndroidInjector
    public abstract ScaledUnitConflictFragment ScaledUnitConflictFragment();

    @ContributesAndroidInjector
    public abstract OutlineFragment OutlineFragment();

    @ContributesAndroidInjector
    public abstract FoodAddFragment FoodAddFragment();

    @ContributesAndroidInjector
    public abstract EmptyFoodFragment EmptyFoodFragment();

    @ContributesAndroidInjector
    public abstract FoodEditFragment FoodEditFragment();

    @ContributesAndroidInjector
    public abstract FoodConflictFragment FoodConflictFragment();

    @ContributesAndroidInjector
    public abstract FoodInLocationFragment FoodInLocationFragment();

    @ContributesAndroidInjector
    public abstract AllFoodFragment AllFoodFragment();

    @ContributesAndroidInjector
    public abstract FoodItemListFragment FoodItemListFragment();

    @ContributesAndroidInjector
    public abstract FoodItemAddFragment FoodItemAddFragment();

    @ContributesAndroidInjector
    public abstract FoodItemTabsFragment FoodItemTabsFragment();

    @ContributesAndroidInjector
    public abstract FoodItemEditFragment FoodItemEditFragment();

    @ContributesAndroidInjector
    public abstract FoodItemConflictFragment FoodItemConflictFragment();

    @ContributesAndroidInjector
    public abstract UserListFragment UserListFragment();

    @ContributesAndroidInjector
    public abstract UserDeviceListFragment UserDeviceListFragment();

    @ContributesAndroidInjector
    public abstract RecipeListFragment RecipeListFragment();

    @ContributesAndroidInjector
    public abstract EanNumberListFragment EanNumberListFragment();

    @ContributesAndroidInjector
    public abstract RecipeAddFragment RecipeAddFragment();

    @ContributesAndroidInjector
    public abstract SettingsFragment SettingsFragment();

    @ContributesAndroidInjector
    public abstract CrashLogListFragment CrashLogListFragment();

    @ContributesAndroidInjector
    public abstract SearchedFoodFragment SearchedFoodFragment();

    @ContributesAndroidInjector
    public abstract ShoppingListFragment ShoppingListFragment();

    @ContributesAndroidInjector
    public abstract HistoryFragment HistoryFragment();

    @ContributesAndroidInjector
    public abstract FoodEanNumberAssignmentFragment FoodEanNumberAssignmentFragment();

    @ContributesAndroidInjector
    public abstract RecipeDetailFragment RecipeDetailFragment();

    @ContributesAndroidInjector
    public abstract UserAddFragment UserAddFragment();

    @ContributesAndroidInjector
    public abstract UserDeviceAddFragment UserDeviceAddFragment();

    @ContributesAndroidInjector
    public abstract TicketShowFragment TicketShowFragment();

    @ContributesAndroidInjector
    public abstract FoodDetailsFragment FoodDetailsFragment();

    @ContributesAndroidInjector
    public abstract RecipeEditFragment RecipeEditFragment();

    @ContributesAndroidInjector
    public abstract RecipeCookFragment RecipeCookFragment();
}
