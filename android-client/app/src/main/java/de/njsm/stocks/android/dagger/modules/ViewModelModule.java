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

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import de.njsm.stocks.android.dagger.annotations.ViewModelKey;
import de.njsm.stocks.android.dagger.factories.ViewModelFactory;
import de.njsm.stocks.android.frontend.crashlog.CrashLogViewModel;
import de.njsm.stocks.android.frontend.device.SingleUserViewModel;
import de.njsm.stocks.android.frontend.device.UserDeviceViewModel;
import de.njsm.stocks.android.frontend.eannumber.EanNumberViewModel;
import de.njsm.stocks.android.frontend.emptyfood.EmptyFoodViewModel;
import de.njsm.stocks.android.frontend.emptyfood.FoodViewModel;
import de.njsm.stocks.android.frontend.food.FoodToEatViewModel;
import de.njsm.stocks.android.frontend.fooditem.FoodItemViewModel;
import de.njsm.stocks.android.frontend.locations.LocationViewModel;
import de.njsm.stocks.android.frontend.search.SearchViewModel;
import de.njsm.stocks.android.frontend.user.UserViewModel;
import de.njsm.stocks.android.frontend.util.RefreshViewModel;
import de.njsm.stocks.android.repo.*;

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
    @ViewModelKey(UserViewModel.class)
    ViewModel provideUsersViewModel(UserRepository repo) {
        UserViewModel result = new UserViewModel(repo);
        result.init();
        return result;
    }

    @Provides
    @IntoMap
    @ViewModelKey(EmptyFoodViewModel.class)
    ViewModel provideEmptyFoodViewModel(FoodRepository repo) {
        EmptyFoodViewModel result = new EmptyFoodViewModel(repo);
        result.init();
        return result;
    }

    @Provides
    @IntoMap
    @ViewModelKey(FoodToEatViewModel.class)
    ViewModel provideFoodToEatViewModel(FoodRepository repo) {
        FoodToEatViewModel result = new FoodToEatViewModel(repo);
        result.init();
        return result;
    }

    @Provides
    @IntoMap
    @ViewModelKey(FoodViewModel.class)
    ViewModel provideFoodViewModel(FoodRepository repo) {
        return new FoodViewModel(repo);
    }

    @Provides
    @IntoMap
    @ViewModelKey(SearchViewModel.class)
    ViewModel provideSearchViewModel(FoodRepository repo) {
        return new SearchViewModel(repo);
    }

    @Provides
    @IntoMap
    @ViewModelKey(EanNumberViewModel.class)
    ViewModel provideEanNumberViewModel(EanNumberRepository repo) {
        return new EanNumberViewModel(repo);
    }

    @Provides
    @IntoMap
    @ViewModelKey(FoodItemViewModel.class)
    ViewModel provideFoodItemViewModel(FoodItemRepository repo) {
        return new FoodItemViewModel(repo);
    }

    @Provides
    @IntoMap
    @ViewModelKey(UserDeviceViewModel.class)
    ViewModel provideUserDeviceViewModel(UserDeviceRepository repo) {
        return new UserDeviceViewModel(repo);
    }

    @Provides
    @IntoMap
    @ViewModelKey(SingleUserViewModel.class)
    ViewModel provideUserViewModel(UserRepository repo) {
        return new SingleUserViewModel(repo);
    }

    @Provides
    @IntoMap
    @ViewModelKey(LocationViewModel.class)
    ViewModel provideLocationViewModel(LocationRepository repo) {
        LocationViewModel result = new LocationViewModel(repo);
        result.init();
        return result;
    }

    @Provides
    @IntoMap
    @ViewModelKey(RefreshViewModel.class)
    ViewModel provideRefreshViewModel(RefreshRepository repo) {
        return new RefreshViewModel(repo);
    }

    @Provides
    @IntoMap
    @ViewModelKey(CrashLogViewModel.class)
    ViewModel provideCrashLogViewModel(CrashLogRepository repo) {
        CrashLogViewModel result = new CrashLogViewModel(repo);
        result.init();
        return result;
    }
}