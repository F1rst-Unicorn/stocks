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


import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.LocationForListing;
import de.njsm.stocks.client.navigation.LocationListNavigator;
import de.njsm.stocks.client.presenter.LocationViewModel;
import de.njsm.stocks.client.ui.R;
import de.njsm.stocks.client.view.listswipe.SwipeCallback;

import javax.inject.Inject;
import java.util.List;

public class LocationListFragment extends BottomToolbarFragment {

    private LocationViewModel locationViewModel;

    private LocationListNavigator locationListNavigator;

    private LocationAdapter locationListAdapter;

    private TemplateSwipeList templateSwipeList;

    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        View swipeList = insertContent(inflater, root, R.layout.template_swipe_list);
        templateSwipeList = new TemplateSwipeList(swipeList);
        templateSwipeList.setLoading();

        locationListAdapter = new LocationAdapter(this::onItemClicked, this::onItemLongClicked);
        locationViewModel.getLocations().observe(getViewLifecycleOwner(), this::onListDataReceived);

        SwipeCallback callback = new SwipeCallback(
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_delete_white_24dp),
                new ColorDrawable(ContextCompat.getColor(requireActivity(), R.color.colorAccent)),
                this::onItemSwipedRight
        );

        templateSwipeList.initialiseListWithSwiper(requireContext(), locationListAdapter, callback);
        templateSwipeList.bindFloatingActionButton(this::onAddItem);
        templateSwipeList.bindSwipeDown(this::onSwipeDown);

        return root;
    }

    private void onListDataReceived(List<LocationForListing> data) {
        if (data.isEmpty()) {
            templateSwipeList.setEmpty(R.string.hint_no_locations);
        } else {
            templateSwipeList.setList();
        }
        locationListAdapter.setData(data);
    }

    private void onItemSwipedRight(int listItemPosition) {
        locationViewModel.deleteLocation(listItemPosition);
    }

    private void onItemClicked(View listItem) {
        int listItemIndex = ((ViewHolder) listItem.getTag()).getBindingAdapterPosition();
        locationViewModel.resolveLocationId(listItemIndex, v -> locationListNavigator.showLocation(v));
    }

    private boolean onItemLongClicked(View listItem) {
        int listItemIndex = ((ViewHolder) listItem.getTag()).getBindingAdapterPosition();
        locationViewModel.resolveLocationId(listItemIndex, v -> locationListNavigator.editLocation(v));
        return true;
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
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        locationViewModel = viewModelProvider.get(LocationViewModel.class);
    }
}
