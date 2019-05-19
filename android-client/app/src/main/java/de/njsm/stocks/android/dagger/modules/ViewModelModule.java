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
import de.njsm.stocks.android.frontend.food.FoodToEatViewModel;
import de.njsm.stocks.android.frontend.emptyfood.EmptyFoodViewModel;
import de.njsm.stocks.android.frontend.emptyfood.FoodViewModel;
import de.njsm.stocks.android.frontend.fooditem.FoodItemViewModel;
import de.njsm.stocks.android.frontend.locations.LocationViewModel;
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
