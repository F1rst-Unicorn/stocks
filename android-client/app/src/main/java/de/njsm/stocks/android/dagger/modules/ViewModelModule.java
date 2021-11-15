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
import de.njsm.stocks.android.frontend.fooditem.PlotViewModel;
import de.njsm.stocks.android.frontend.locations.LocationViewModel;
import de.njsm.stocks.android.frontend.main.EventViewModel;
import de.njsm.stocks.android.frontend.recipe.RecipeViewModel;
import de.njsm.stocks.android.frontend.recipedetail.RecipeIngredientViewModel;
import de.njsm.stocks.android.frontend.recipedetail.RecipeProductRepository;
import de.njsm.stocks.android.frontend.recipedetail.RecipeProductViewModel;
import de.njsm.stocks.android.frontend.search.SearchViewModel;
import de.njsm.stocks.android.frontend.settings.SettingsUpdaterViewModel;
import de.njsm.stocks.android.frontend.shoppinglist.FoodToBuyViewModel;
import de.njsm.stocks.android.frontend.units.ScaledUnitViewModel;
import de.njsm.stocks.android.frontend.units.UnitViewModel;
import de.njsm.stocks.android.frontend.user.UserViewModel;
import de.njsm.stocks.android.frontend.util.RefreshViewModel;
import de.njsm.stocks.android.network.server.HostnameInterceptor;
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
    @ViewModelKey(FoodToBuyViewModel.class)
    ViewModel provideFoodToBuyViewModel(FoodRepository repo) {
        FoodToBuyViewModel result = new FoodToBuyViewModel(repo);
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
    @ViewModelKey(SettingsUpdaterViewModel.class)
    ViewModel provideSettingsUpdaterViewModel(HostnameInterceptor repo) {
        return new SettingsUpdaterViewModel(repo);
    }

    @Provides
    @IntoMap
    @ViewModelKey(CrashLogViewModel.class)
    ViewModel provideCrashLogViewModel(CrashLogRepository repo) {
        CrashLogViewModel result = new CrashLogViewModel(repo);
        result.init();
        return result;
    }

    @Provides
    @IntoMap
    @ViewModelKey(EventViewModel.class)
    ViewModel provideEventsViewModel(EventRepository repo) {
        EventViewModel result = new EventViewModel(repo);
        result.init();
        return result;
    }

    @Provides
    @IntoMap
    @ViewModelKey(de.njsm.stocks.android.frontend.foodhistory.EventViewModel.class)
    ViewModel provideFoodEventsViewModel(EventRepository repo) {
        return new de.njsm.stocks.android.frontend.foodhistory.EventViewModel(repo);
    }

    @Provides
    @IntoMap
    @ViewModelKey(de.njsm.stocks.android.frontend.locationhistory.EventViewModel.class)
    ViewModel provideLocationEventsViewModel(EventRepository repo) {
        return new de.njsm.stocks.android.frontend.locationhistory.EventViewModel(repo);
    }

    @Provides
    @IntoMap
    @ViewModelKey(PlotViewModel.class)
    ViewModel providePlotViewModel(PlotRepository repo) {
        return new PlotViewModel(repo);
    }

    @Provides
    @IntoMap
    @ViewModelKey(UnitViewModel.class)
    ViewModel provideUnitViewModel(UnitRepository repo) {
        UnitViewModel result = new UnitViewModel(repo);
        result.init();
        return result;
    }

    @Provides
    @IntoMap
    @ViewModelKey(ScaledUnitViewModel.class)
    ViewModel provideScaledUnitViewModel(ScaledUnitRepository repo) {
        ScaledUnitViewModel result = new ScaledUnitViewModel(repo);
        result.init();
        return result;
    }

    @Provides
    @IntoMap
    @ViewModelKey(RecipeViewModel.class)
    ViewModel provideRecipeViewModel(RecipeRepository repo) {
        RecipeViewModel result = new RecipeViewModel(repo);
        result.init();
        return result;
    }

    @Provides
    @IntoMap
    @ViewModelKey(RecipeIngredientViewModel.class)
    ViewModel provideRecipeIngredientViewModel(RecipeIngredientRepository repo) {
        RecipeIngredientViewModel result = new RecipeIngredientViewModel(repo);
        return result;
    }

    @Provides
    @IntoMap
    @ViewModelKey(RecipeProductViewModel.class)
    ViewModel provideRecipeProductViewModel(RecipeProductRepository repo) {
        RecipeProductViewModel result = new RecipeProductViewModel(repo);
        return result;
    }

    @Provides
    @IntoMap
    @ViewModelKey(de.njsm.stocks.android.frontend.recipecheckout.ViewModel.class)
    ViewModel provideRecipeCheckoutViewModel(RecipeIngredientRepository repo, FoodRepository foodRepository, FoodItemRepository foodItemRepository) {
        return new de.njsm.stocks.android.frontend.recipecheckout.ViewModel(repo, foodRepository, foodItemRepository);
    }

    @Provides
    @IntoMap
    @ViewModelKey(de.njsm.stocks.android.frontend.unithistory.EventViewModel.class)
    ViewModel provideUnitEventViewModel(EventRepository repo) {
        de.njsm.stocks.android.frontend.unithistory.EventViewModel result = new de.njsm.stocks.android.frontend.unithistory.EventViewModel(repo);
        result.init();
        return result;
    }

}
