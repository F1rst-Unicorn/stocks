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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.client.navigation.LocationListNavigator;
import de.njsm.stocks.client.presenter.LocationViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;

public class LocationListFragment extends Fragment {

    private LocationViewModel locationViewModel;

    private LocationListNavigator locationListNavigator;

    @Override
    public void onAttach(@NonNull Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.template_swipe_list, container, false);

        return result;
    }

    private void onItemSwipedRight(View listItem) {
        int listItemPosition = ((ViewHolder) listItem.getTag()).getBindingAdapterPosition();
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
