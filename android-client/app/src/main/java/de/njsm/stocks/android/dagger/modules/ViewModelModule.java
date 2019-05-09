package de.njsm.stocks.android.dagger.modules;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import de.njsm.stocks.android.dagger.annotations.ViewModelKey;
import de.njsm.stocks.android.dagger.factories.ViewModelFactory;
import de.njsm.stocks.android.frontend.user.UserViewModel;
import de.njsm.stocks.android.frontend.util.RefreshViewModel;
import de.njsm.stocks.android.repo.RefreshRepo;
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
    ViewModel provideUserViewModel(UserRepository repo) {
        UserViewModel result = new UserViewModel(repo);
        result.init();
        return result;
    }

    @Provides
    @IntoMap
    @ViewModelKey(RefreshViewModel.class)
    ViewModel provideRefreshViewModel(RefreshRepo repo) {
        return new RefreshViewModel(repo);
    }
}
