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
 *
 */

package de.njsm.stocks.client.view;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.client.navigation.LocationListNavigator;
import de.njsm.stocks.client.presenter.LocationViewModel;
import de.njsm.stocks.client.ui.R;
import de.njsm.stocks.client.view.listswipe.SwipeCallback;

import javax.inject.Inject;

public class LocationListFragment extends Fragment {

    private LocationViewModel locationViewModel;

    private LocationListNavigator locationListNavigator;

    private LocationAdapter locationListAdapter;

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
        locationListAdapter = new LocationAdapter();
        locationViewModel.getLocations().observe(getViewLifecycleOwner(), u -> locationListAdapter.setData(u));
        list.setAdapter(locationListAdapter);

        FloatingActionButton addButton = result.findViewById(R.id.template_swipe_list_fab);
        addButton.setOnClickListener(this::onAddItem);

        SwipeCallback callback = new SwipeCallback(
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_delete_white_24dp),
                new ColorDrawable(ContextCompat.getColor(requireActivity(), R.color.colorAccent)),
                this::onItemSwipedRight
        );
        new ItemTouchHelper(callback).attachToRecyclerView(list);

        return result;
    }

    private void onItemSwipedRight(int listItemPosition) {
        locationViewModel.deleteLocation(listItemPosition);
    }

    private void onItemClicked(View listItem) {
        int listItemPosition = ((ViewHolder) listItem.getTag()).getBindingAdapterPosition();

    }

    private void onAddItem(View button) {
        locationListNavigator.addLocation();
    }

    public void onSwipeDown() {
        locationViewModel.synchronise();
    }

    @Inject
    public void setLocationListNavigator(LocationListNavigator locationListNavigator) {
        this.locationListNavigator = locationListNavigator;
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        locationViewModel = viewModelProvider.get(LocationViewModel.class);
    }
}
