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

package de.njsm.stocks.client.di;

import dagger.Module;
import dagger.Provides;
import de.njsm.stocks.client.business.SetupInteractor;
import de.njsm.stocks.client.fragment.DialogDisplayer;
import de.njsm.stocks.client.fragment.setupform.SetupFormFragmentArgumentProvider;
import de.njsm.stocks.client.navigation.*;

import javax.inject.Singleton;

import static org.mockito.Mockito.mock;

@Module
class FakeNavigationModule {

    @Provides
    @Singleton
    LocationListNavigator locationListNavigator() {
        return mock(LocationListNavigator.class);
    }

    @Provides
    @Singleton
    SetupGreetingNavigator setupGreetingNavigator() {
        return mock(SetupGreetingNavigator.class);
    }

    @Provides
    @Singleton
    SetupFormFragmentArgumentProvider setupFormFragmentArgumentProvider() {
        return mock(SetupFormFragmentArgumentProvider.class);
    }

    @Provides
    @Singleton
    SetupInteractor registrationBackend() {
        return mock(SetupInteractor.class);
    }

    @Provides
    @Singleton
    SetupFormNavigator setupFragmentNavigator() {
        return mock(SetupFormNavigator.class);
    }

    @Provides
    @Singleton
    Navigator navigator() {
        return mock(Navigator.class);
    }

    @Provides
    @Singleton
    BottomToolbarNavigator bottomToolbarNavigator() {
        return mock(BottomToolbarNavigator.class);
    }

    @Provides
    @Singleton
    ErrorListNavigator errorListNavigator() {
        return mock(ErrorListNavigator.class);
    }

    @Provides
    @Singleton
    ErrorDetailsNavigator errorDetailsNavigator() {
        return mock(ErrorDetailsNavigator.class);
    }

    @Provides
    @Singleton
    LocationEditNavigator locationEditNavigator() {
        return mock(LocationEditNavigator.class);
    }

    @Provides
    @Singleton
    LocationConflictNavigator locationConflictNavigator() {
        return mock(LocationConflictNavigator.class);
    }

    @Provides
    @Singleton
    UnitListNavigator unitListNavigator() {
        return mock(UnitListNavigator.class);
    }

    @Provides
    @Singleton
    ScaledUnitListNavigator ScaledUnitListNavigator() {
        return mock(ScaledUnitListNavigator.class);
    }

    @Provides
    @Singleton
    UnitEditNavigator UnitEditNavigator() {
        return mock(UnitEditNavigator.class);
    }

    @Provides
    @Singleton
    UnitConflictNavigator UnitConflictNavigator() {
        return mock(UnitConflictNavigator.class);
    }

    @Provides
    @Singleton
    ScaledUnitEditNavigator ScaledUnitEditNavigator() {
        return mock(ScaledUnitEditNavigator.class);
    }

    @Provides
    @Singleton
    ScaledUnitConflictNavigator ScaledUnitConflictNavigator() {
        return mock(ScaledUnitConflictNavigator.class);
    }

    @Provides
    @Singleton
    OutlineNavigator OutlineNavigator() {
        return mock(OutlineNavigator.class);
    }

    @Provides
    @Singleton
    EmptyFoodNavigator EmptyFoodNavigator() {
        return mock(EmptyFoodNavigator.class);
    }

    @Provides
    @Singleton
    FoodEditNavigator FoodEditNavigator() {
        return mock(FoodEditNavigator.class);
    }

    @Provides
    @Singleton
    FoodConflictNavigator FoodConflictNavigator() {
        return mock(FoodConflictNavigator.class);
    }

    @Provides
    @Singleton
    FoodByLocationNavigator FoodByLocationNavigator() {
        return mock(FoodByLocationNavigator.class);
    }

    @Provides
    @Singleton
    AllFoodNavigator AllFoodNavigator() {
        return mock(AllFoodNavigator.class);
    }

    @Provides
    @Singleton
    FoodItemListNavigator FoodItemListNavigator() {
        return mock(FoodItemListNavigator.class);
    }

    @Provides
    @Singleton
    FoodItemAddNavigator FoodItemAddNavigator() {
        return mock(FoodItemAddNavigator.class);
    }

    @Provides
    @Singleton
    DialogDisplayer DialogDisplayer() {
        return mock(DialogDisplayer.class);
    }

    @Provides
    @Singleton
    FoodItemEditNavigator FoodItemEditNavigator() {
        return mock(FoodItemEditNavigator.class);
    }

    @Provides
    @Singleton
    FoodItemConflictNavigator FoodItemConflictNavigator() {
        return mock(FoodItemConflictNavigator.class);
    }

    @Provides
    @Singleton
    UserListNavigator UserListNavigator() {
        return mock(UserListNavigator.class);
    }

    @Provides
    @Singleton
    UserDeviceListNavigator UserDeviceListNavigator() {
        return mock(UserDeviceListNavigator.class);
    }

    @Provides
    @Singleton
    RecipeListNavigator RecipeListNavigator() {
        return mock(RecipeListNavigator.class);
    }

    @Provides
    @Singleton
    EanNumberListNavigator EanNumberListNavigator() {
        return mock(EanNumberListNavigator.class);
    }

    @Provides
    @Singleton
    SettingsNavigator SettingsNavigator() {
        return mock(SettingsNavigator.class);
    }

    @Provides
    @Singleton
    SearchedFoodNavigator SearchedFoodNavigator() {
        return mock(SearchedFoodNavigator.class);
    }

    @Provides
    @Singleton
    ShoppingListNavigator ShoppingListNavigator() {
        return mock(ShoppingListNavigator.class);
    }

    @Provides
    @Singleton
    HistoryNavigator HistoryNavigator() {
        return mock(HistoryNavigator.class);
    }

    @Provides
    @Singleton
    FoodEanNumberAssignmentNavigator FoodEanNumberAssignmentNavigator() {
        return mock(FoodEanNumberAssignmentNavigator.class);
    }

    @Provides
    @Singleton
    RecipeDetailNavigator RecipeDetailNavigator() {
        return mock(RecipeDetailNavigator.class);
    }

    @Provides
    @Singleton
	UserDeviceAddNavigator UserDeviceAddNavigator() {
        return mock(UserDeviceAddNavigator.class);
    }

    @Provides
    @Singleton
    TicketShowNavigator TicketShowNavigator() {
        return mock(TicketShowNavigator.class);
    }

    @Provides
    @Singleton
    FoodDetailsNavigator FoodDetailsNavigator() {
        return mock(FoodDetailsNavigator.class);
    }

    @Provides
    @Singleton
    RecipeEditNavigator RecipeEditNavigator() {
        return mock(RecipeEditNavigator.class);
    }

    @Provides
    @Singleton
    RecipeCookNavigator RecipeCookNavigator() {
        return mock(RecipeCookNavigator.class);
    }
}
