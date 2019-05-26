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

package de.njsm.stocks.android.frontend.main;


import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Food;
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

    private ScanBroadcaseReceiver receiver;

    private FoodViewModel foodViewModel;

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
            TextView view = nav.getHeaderView(0).findViewById(R.id.nav_header_main_username);
            view.setText(settings.getString(Config.USERNAME_CONFIG, ""));
            view = nav.getHeaderView(0).findViewById(R.id.nav_header_main_dev);
            view.setText(settings.getString(Config.DEVICE_NAME_CONFIG, ""));
            view = nav.getHeaderView(0).findViewById(R.id.nav_header_main_server);
            view.setText(settings.getString(Config.SERVER_NAME_CONFIG, ""));

            FoodViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(FoodViewModel.class);
            result.findViewById(R.id.fragment_outline_cardview).setOnClickListener(this::goToEatSoon);
            result.findViewById(R.id.fragment_outline_cardview2).setOnClickListener(this::goToEmptyFood);
            result.findViewById(R.id.fragment_outline_fab).setOnClickListener(v -> this.addFood(viewModel));

            foodViewModel = ViewModelProviders.of(this, viewModelFactory).get(FoodViewModel.class);
        }
        return result;
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }

    private void goToEatSoon(View view) {
        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                .navigate(R.id.action_nav_fragment_outline_to_nav_fragment_food);
    }

    private void goToEmptyFood(View view) {
        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                .navigate(R.id.action_nav_fragment_outline_to_nav_fragment_empty_food);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_outline_options, menu);

        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.fragment_outline_options_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(requireContext(), MainActivity.class)));
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fragment_outline_options_scan:
                if (! probeForCameraPermission()) {
                    return true;
                }
                if (receiver == null) {
                    receiver = new ScanBroadcaseReceiver(this::goToScannedFood);
                }
                IntentFilter filter = new IntentFilter(MainActivity.ACTION_QR_CODE_SCANNED);
                LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, filter);
                LOG.i("Starting QR code reader");
                IntentIntegrator integrator = new IntentIntegrator(getActivity());
                integrator.initiateScan();
        }
        return true;
    }

    private void goToScannedFood(String s) {
        if (getContext() == null)
            return;

        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver);

        if (s == null)
            return;

        LiveData<Food> scannedFood = foodViewModel.getFoodByEanNumber(s);
        scannedFood.observe(this, f -> {
            scannedFood.removeObservers(this);
            NavDirections args;
            if (f != null) {
                LOG.d("Found scanned food as " + f.name);
                args = OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentFoodItem(f.id);
            } else {
                LOG.d("No food found");
                args = OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentAllFood(s);
            }

            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                    .navigate(args);
        });
    }
}
