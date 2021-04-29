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

package de.njsm.stocks.android.frontend.eannumber;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.integration.android.IntentIntegrator;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.emptyfood.FoodViewModel;
import de.njsm.stocks.android.frontend.interactor.EanNumberDeletionInteractor;
import de.njsm.stocks.android.frontend.main.MainActivity;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.util.Logger;
import de.njsm.stocks.android.util.idling.IdlingResource;

public class EanNumberFragment extends BaseFragment {

    private static final Logger LOG = new Logger(EanNumberFragment.class);

    private ViewModelProvider.Factory viewModelFactory;

    private EanNumberViewModel viewModel;

    private EanNumberAdapter adapter;

    private EanNumberBroadcastReceiver receiver;

    private EanNumberFragmentArgs input;

    private IdlingResource resource;

    @Override
    public void onAttach(@NonNull Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.template_swipe_list, container, false);

        RecyclerView list = result.findViewById(R.id.template_swipe_list_list);
        list.setLayoutManager(new LinearLayoutManager(requireContext()));

        assert getArguments() != null;
        input = EanNumberFragmentArgs.fromBundle(getArguments());

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(EanNumberViewModel.class);
        FoodViewModel foodViewModel = ViewModelProviders.of(this, viewModelFactory).get(FoodViewModel.class);
        viewModel.init(input.getFoodId());
        foodViewModel.getFood(input.getFoodId()).observe(getViewLifecycleOwner(), f -> {
            String title = getString(R.string.title_barcode_fragment, f.name);
            requireActivity().setTitle(title);
        });

        adapter = new EanNumberAdapter(viewModel.getData(), this::doNothing);
        viewModel.getData().observe(getViewLifecycleOwner(), v -> adapter.notifyDataSetChanged());
        list.setAdapter(adapter);

        EanNumberDeletionInteractor deleter = new EanNumberDeletionInteractor(this, result, v -> adapter.notifyDataSetChanged(), viewModel::deleteEanNumber);
        addSwipeToDelete(list, viewModel.getData(), deleter::initiateDeletion);
        result.findViewById(R.id.template_swipe_list_fab).setOnClickListener(this::startScanning);
        receiver = new EanNumberBroadcastReceiver(this::addEanNumber);
        IntentFilter filter = new IntentFilter(MainActivity.ACTION_QR_CODE_SCANNED);
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, filter);
        initialiseSwipeRefresh(result, viewModelFactory);
        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver);
    }

    private void startScanning(View dummy) {
        LOG.i("Starting QR code reader");
        if (probeForCameraPermission()) {
            resource.increment();
            IntentIntegrator integrator = new IntentIntegrator(getActivity());
            integrator.initiateScan();
        }
    }

    private void addEanNumber(String code) {
        if (code != null) {
            LiveData<StatusCode> result = viewModel.addEanNumber(code, input.getFoodId());
            result.observe(this, this::maybeShowAddError);
        }
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }

    @Inject
    public void setResource(IdlingResource resource) {
        this.resource = resource;
    }
}
