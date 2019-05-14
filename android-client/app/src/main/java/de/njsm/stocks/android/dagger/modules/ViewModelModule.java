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
import de.njsm.stocks.android.frontend.user.UserViewModel;
import de.njsm.stocks.android.frontend.util.RefreshViewModel;
import de.njsm.stocks.android.repo.CrashLogRepository;
import de.njsm.stocks.android.repo.RefreshRepository;
import de.njsm.stocks.android.repo.UserDeviceRepository;
import de.njsm.stocks.android.repo.UserRepository;

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
