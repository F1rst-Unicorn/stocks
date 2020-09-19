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

package de.njsm.stocks.android.frontend.locationhistory;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.foodhistory.EventAdapter;
import de.njsm.stocks.android.frontend.locations.LocationViewModel;

public class LocationHistoryFragment extends BaseFragment {

    private ViewModelProvider.Factory viewModelFactory;

    @Override
    public void onAttach(@NonNull Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.template_swipe_list, container, false);

        assert getArguments() != null;
        LocationHistoryFragmentArgs input = LocationHistoryFragmentArgs.fromBundle(getArguments());

        result.findViewById(R.id.template_swipe_list_fab).setVisibility(View.GONE);

        RecyclerView list = result.findViewById(R.id.template_swipe_list_list);
        list.setLayoutManager(new LinearLayoutManager(requireActivity()));
        EventViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(EventViewModel.class);
        viewModel.init(input.getLocationId());
        EventAdapter adapter = new EventAdapter(getResources(), requireActivity().getTheme(), requireContext()::getString);
        viewModel.getHistory().observe(getViewLifecycleOwner(), adapter::submitList);
        list.setAdapter(adapter);

        LocationViewModel foodViewModel = ViewModelProviders.of(this, viewModelFactory).get(LocationViewModel.class);
        foodViewModel.getLocation(input.getLocationId()).observe(getViewLifecycleOwner(), u -> requireActivity().setTitle(u == null ? "" : String.format(getString(R.string.title_food_activity), u.name)));

        initialiseSwipeRefresh(result, viewModelFactory);
        return result;
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }
}
