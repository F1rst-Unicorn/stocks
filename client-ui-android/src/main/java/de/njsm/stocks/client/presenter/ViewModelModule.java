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

package de.njsm.stocks.client.presenter;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import de.njsm.stocks.client.business.*;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.business.event.EventInteractor;
import de.njsm.stocks.client.business.event.EventInteractorFactory;
import de.njsm.stocks.client.business.event.UnitEventInteractor;
import de.njsm.stocks.client.di.ViewModelFactory;
import de.njsm.stocks.client.di.ViewModelKey;
import de.njsm.stocks.client.execution.SchedulerStatusReporter;

import javax.inject.Provider;
import java.util.Map;

@Module
public class ViewModelModule {

    @Provides
    ViewModelProvider.Factory viewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> providerMap) {
        return new ViewModelFactory(providerMap);
    }

    @Provides
    @IntoMap
    @ViewModelKey(LocationListViewModel.class)
    ViewModel locationViewModel(LocationListInteractor locationListInteractor, EntityDeleter<Location> locationDeleter, Synchroniser synchroniser) {
        return new LocationListViewModel(locationListInteractor, locationDeleter, synchroniser);
    }

    @Provides
    @IntoMap
    @ViewModelKey(SetupViewModel.class)
    ViewModel setupViewModel(SetupInteractor setupInteractor) {
        return new SetupViewModel(setupInteractor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(LocationAddViewModel.class)
    ViewModel locationAddViewModel(LocationAddInteractor locationAddInteractor) {
        return new LocationAddViewModel(locationAddInteractor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(ToolbarViewModel.class)
    ViewModel toolbarViewModel(SchedulerStatusReporter schedulerStatusReporter, ErrorStatusReporter errorStatusReporter) {
        return new ToolbarViewModel(schedulerStatusReporter, errorStatusReporter);
    }

    @Provides
    @IntoMap
    @ViewModelKey(ErrorListViewModel.class)
    ViewModel errorListViewModel(Synchroniser synchroniser, ErrorRetryInteractor errorRetryInteractor, ErrorListInteractor errorListInteractor) {
        return new ErrorListViewModel(errorListInteractor, errorRetryInteractor, synchroniser);
    }

    @Provides
    @IntoMap
    @ViewModelKey(ErrorDetailsViewModel.class)
    ViewModel errorDetailsViewModel(ErrorListInteractor errorListInteractor, ErrorRetryInteractor errorRetryInteractor) {
        return new ErrorDetailsViewModel(errorListInteractor, errorRetryInteractor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(LocationEditViewModel.class)
    ViewModel locationEditViewModel(LocationEditInteractor locationEditInteractor) {
        return new LocationEditViewModel(locationEditInteractor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(LocationConflictViewModel.class)
    ViewModel locationConflictViewModel(LocationConflictInteractor locationConflictInteractor, ErrorRetryInteractor errorRetryInteractor) {
        return new LocationConflictViewModel(locationConflictInteractor, errorRetryInteractor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(UnitListViewModel.class)
    ViewModel UnitListViewModel(UnitListInteractor unitListInteractor, EntityDeleter<Unit> unitDeleter) {
        return new UnitListViewModel(unitListInteractor, unitDeleter);
    }

    @Provides
    @IntoMap
    @ViewModelKey(ScaledUnitListViewModel.class)
    ViewModel ScaledUnitListViewModel(ScaledUnitListInteractor scaledUnitListInteractor, EntityDeleter<ScaledUnit> scaledUnitDeleter) {
        return new ScaledUnitListViewModel(scaledUnitListInteractor, scaledUnitDeleter);
    }

    @Provides
    @IntoMap
    @ViewModelKey(TabsViewModel.class)
    ViewModel UnitTabsViewModel(Synchroniser synchroniser) {
        return new TabsViewModel(synchroniser);
    }

    @Provides
    @IntoMap
    @ViewModelKey(UnitAddViewModel.class)
    ViewModel UnitAddViewModel(UnitAddInteractor unitAddInteractor) {
        return new UnitAddViewModel(unitAddInteractor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(UnitEditViewModel.class)
    ViewModel UnitEditViewModel(UnitEditInteractor unitEditInteractor) {
        return new UnitEditViewModel(unitEditInteractor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(UnitConflictViewModel.class)
    ViewModel UnitConflictViewModel(UnitConflictInteractor unitConflictInteractor, ErrorRetryInteractor errorRetryInteractor) {
        return new UnitConflictViewModel(unitConflictInteractor, errorRetryInteractor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(ScaledUnitAddViewModel.class)
    ViewModel ScaledUnitAddViewModel(ScaledUnitAddInteractor scaledUnitAddInteractor) {
        return new ScaledUnitAddViewModel(scaledUnitAddInteractor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(ScaledUnitEditViewModel.class)
    ViewModel ScaledUnitEditViewModel(ScaledUnitEditInteractor scaledUnitEditInteractor) {
        return new ScaledUnitEditViewModel(scaledUnitEditInteractor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(ScaledUnitConflictViewModel.class)
    ViewModel ScaledUnitConflictViewModel(ScaledUnitConflictInteractor scaledUnitConflictInteractor, ErrorRetryInteractor errorRetryInteractor) {
        return new ScaledUnitConflictViewModel(scaledUnitConflictInteractor, errorRetryInteractor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(OutlineViewModel.class)
    ViewModel OutlineViewModel(Synchroniser synchroniser, Localiser localiser, EventInteractor interactor, EanNumberLookupInteractor lookupInteractor) {
        return new OutlineViewModel(synchroniser, localiser, interactor, lookupInteractor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(FoodAddViewModel.class)
    ViewModel FoodAddViewModel(FoodAddInteractor interactor) {
        return new FoodAddViewModel(interactor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(EmptyFoodViewModel.class)
    ViewModel EmptyFoodViewModel(EmptyFoodInteractor emptyFoodInteractor, Synchroniser synchroniser, EntityDeleter<Food> deleter, FoodToBuyInteractor toBuyInteractor) {
        return new EmptyFoodViewModel(synchroniser, emptyFoodInteractor, deleter, toBuyInteractor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(FoodEditViewModel.class)
    ViewModel FoodEditViewModel(FoodEditInteractor interactor) {
        return new FoodEditViewModel(interactor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(FoodConflictViewModel.class)
    ViewModel FoodConflictViewModel(FoodConflictInteractor conflictInteractor, ErrorRetryInteractor errorRetryInteractor) {
        return new FoodConflictViewModel(conflictInteractor, errorRetryInteractor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(FoodByLocationListViewModel.class)
    ViewModel FoodByLocationListViewModel(FoodByLocationListInteractor foodByLocationListInteractor, Synchroniser synchroniser, EntityDeleter<Food> deleter, FoodToBuyInteractor toBuyInteractor) {
        return new FoodByLocationListViewModel(synchroniser, foodByLocationListInteractor, deleter, toBuyInteractor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(AllFoodListViewModel.class)
    ViewModel AllFoodListViewModel(Synchroniser synchroniser, AllPresentFoodListInteractor allPresentFoodListInteractor, EntityDeleter<Food> deleter, FoodToBuyInteractor toBuyInteractor) {
        return new AllFoodListViewModel(synchroniser, allPresentFoodListInteractor, deleter, toBuyInteractor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(FoodItemListViewModel.class)
    ViewModel FoodItemListViewModel(FoodItemListInteractor interactor, EntityDeleter<FoodItem> deleter, FoodToBuyInteractor toBuyInteractor) {
        return new FoodItemListViewModel(interactor, deleter, toBuyInteractor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(FoodItemAddViewModel.class)
    ViewModel FoodItemAddViewModel(FoodItemAddInteractor interactor) {
        return new FoodItemAddViewModel(interactor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(FoodItemEditViewModel.class)
    ViewModel FoodItemEditViewModel(FoodItemEditInteractor interactor) {
        return new FoodItemEditViewModel(interactor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(FoodItemConflictViewModel.class)
    ViewModel FoodItemConflictViewModel(FoodItemConflictInteractor interactor, ErrorRetryInteractor errorRetryInteractor) {
        return new FoodItemConflictViewModel(interactor, errorRetryInteractor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(UserListViewModel.class)
    ViewModel UserListViewModel(UserListInteractor interactor, Synchroniser errorRetryInteractor, EntityDeleter<User> deleter) {
        return new UserListViewModel(interactor, deleter, errorRetryInteractor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(UserDeviceListViewModel.class)
    ViewModel UserDeviceListViewModel(UserDeviceListInteractor interactor, Synchroniser errorRetryInteractor, EntityDeleter<UserDevice> deleter) {
        return new UserDeviceListViewModel(interactor, deleter, errorRetryInteractor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(RecipeListViewModel.class)
    ViewModel RecipeListViewModel(RecipeListInteractor interactor, Synchroniser errorRetryInteractor) {
        return new RecipeListViewModel(interactor, errorRetryInteractor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(EanNumberListViewModel.class)
    ViewModel EanNumberListViewModel(EanNumberListInteractor interactor, Synchroniser synchroniser, EntityDeleter<EanNumber> deleter) {
        return new EanNumberListViewModel(interactor, deleter, synchroniser);
    }

    @Provides
    @IntoMap
    @ViewModelKey(RecipeAddViewModel.class)
    ViewModel RecipeAddViewModel(RecipeAddInteractor interactor) {
        return new RecipeAddViewModel(interactor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(SettingsViewModel.class)
    ViewModel SettingsViewModel(SettingsInteractor interactor) {
        return new SettingsViewModel(interactor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(CrashLogListViewModel.class)
    ViewModel CrashLogListViewModel(CrashListInteractor interactor) {
        return new CrashLogListViewModel(interactor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(SearchedFoodViewModel.class)
    ViewModel SearchedFoodViewModel(Synchroniser synchroniser, SearchInteractor interactor, EntityDeleter<Food> deleter, FoodToBuyInteractor toBuyInteractor) {
        return new SearchedFoodViewModel(synchroniser, interactor, deleter, toBuyInteractor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(ShoppingListViewModel.class)
    ViewModel ShoppingListViewModel(Synchroniser synchroniser, FoodToBuyInteractor interactor) {
        return new ShoppingListViewModel(synchroniser, interactor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(HistoryViewModel.class)
    ViewModel HistoryViewModel(Localiser localiser, UnitEventInteractor interactor, EventInteractorFactory factory) {
        return new HistoryViewModel(localiser, interactor, factory);
    }

    @Provides
    @IntoMap
    @ViewModelKey(FoodEanNumberAssignmentViewModel.class)
    ViewModel FoodEanNumberAssignmentViewModel(EanNumberAssignmentInteractor interactor, Synchroniser synchroniser) {
        return new FoodEanNumberAssignmentViewModel(interactor, synchroniser);
    }

    @Provides
    @IntoMap
    @ViewModelKey(RecipeDetailViewModel.class)
    ViewModel RecipeDetailViewModel(Synchroniser synchroniser, RecipeDetailInteractor interactor) {
        return new RecipeDetailViewModel(synchroniser, interactor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(UserAddViewModel.class)
    ViewModel UserAddViewModel(UserAddInteractor interactor) {
        return new UserAddViewModel(interactor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(UserDeviceAddViewModel.class)
    ViewModel UserDeviceAddViewModel(UserDeviceAddInteractor interactor) {
        return new UserDeviceAddViewModel(interactor);
    }
}
