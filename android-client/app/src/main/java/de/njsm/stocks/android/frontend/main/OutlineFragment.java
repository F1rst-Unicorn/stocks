package de.njsm.stocks.android.frontend.main;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import com.google.android.material.navigation.NavigationView;
import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.emptyfood.FoodViewModel;
import de.njsm.stocks.android.frontend.util.RefreshViewModel;
import de.njsm.stocks.android.util.Config;
import de.njsm.stocks.android.util.Logger;

import javax.inject.Inject;

public class OutlineFragment extends BaseFragment {

    private static final Logger LOG = new Logger(OutlineFragment.class);

    private ViewModelProvider.Factory viewModelFactory;

    @Inject
    SharedPreferences settings;

    private boolean initialised = false;

    @Override
    public void onAttach(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Config.PREFERENCES_FILE, Context.MODE_PRIVATE);
        if (! prefs.contains(Config.USERNAME_CONFIG)) {
            LOG.i("First start up, redirecting to startup fragment");
            initialised = false;
        } else {
            AndroidSupportInjection.inject(this);
            initialised = true;
        }
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_outline, container, false);
        if (!initialised) {
            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment).navigate(R.id.nav_fragment_startup);
        } else {
            RefreshViewModel refresher = initialiseSwipeRefresh(result, R.id.fragment_outline_swipe, viewModelFactory);
            refresher.refresh();
            setHasOptionsMenu(true);
            NavigationView nav = requireActivity().findViewById(R.id.main_nav);
            TextView view = nav.getHeaderView(0).findViewById(R.id.drawer_username);
            view.setText(settings.getString(Config.USERNAME_CONFIG, ""));
            view = nav.getHeaderView(0).findViewById(R.id.drawer_user_dev);
            view.setText(settings.getString(Config.DEVICE_NAME_CONFIG, ""));
            view = nav.getHeaderView(0).findViewById(R.id.drawer_server);
            view.setText(settings.getString(Config.SERVER_NAME_CONFIG, ""));

            FoodViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(FoodViewModel.class);
            result.findViewById(R.id.fragment_outline_cardview2).setOnClickListener(this::goToEmptyFood);
            result.findViewById(R.id.fragment_outline_fab).setOnClickListener(v -> this.addFood(viewModel));
        }
        return result;
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }

    private void goToEmptyFood(View view) {
        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                .navigate(R.id.action_nav_fragment_outline_to_nav_fragment_empty_food);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar_outline, menu);

        /*SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class)));
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(true);*/
    }
}
