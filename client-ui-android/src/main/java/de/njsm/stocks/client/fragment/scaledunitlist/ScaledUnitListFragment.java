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

package de.njsm.stocks.client.fragment.scaledunitlist;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.client.business.entities.ScaledUnitForListing;
import de.njsm.stocks.client.fragment.InjectableFragment;
import de.njsm.stocks.client.fragment.listswipe.SwipeCallback;
import de.njsm.stocks.client.fragment.view.TemplateSwipeList;
import de.njsm.stocks.client.navigation.ScaledUnitListNavigator;
import de.njsm.stocks.client.presenter.ScaledUnitListViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;
import java.util.List;

public class ScaledUnitListFragment extends InjectableFragment {

    private ScaledUnitListViewModel scaledUnitListViewModel;

    private ScaledUnitListNavigator scaledUnitListNavigator;

    private ScaledUnitListAdapter scaledUnitListAdapter;

    private TemplateSwipeList templateSwipeList;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.template_swipe_list, container, false);
        templateSwipeList = new TemplateSwipeList(root);
        templateSwipeList.setLoading();
        templateSwipeList.disableSwipeRefresh();

        scaledUnitListAdapter = new ScaledUnitListAdapter(this::onItemClicked);
        scaledUnitListViewModel.getScaledUnits().observe(getViewLifecycleOwner(), this::onListDataReceived);

        SwipeCallback callback = new SwipeCallback(
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_delete_white_24dp),
                new ColorDrawable(ContextCompat.getColor(requireActivity(), R.color.colorAccent)),
                this::onItemSwipedRight
        );

        templateSwipeList.initialiseListWithSwiper(requireContext(), scaledUnitListAdapter, callback);
        templateSwipeList.bindFloatingActionButton(this::onAddItem);

        return root;
    }

    private void onListDataReceived(List<ScaledUnitForListing> unitForListings) {
        if (unitForListings.isEmpty()) {
            templateSwipeList.setEmpty(R.string.hint_no_scaled_units);
        } else {
            templateSwipeList.setList();
        }
        scaledUnitListAdapter.setData(unitForListings);
    }

    private void onItemSwipedRight(int listItemIndex) {
        scaledUnitListViewModel.deleteScaledUnit(listItemIndex);
    }

    private void onItemClicked(View listItem) {
        int listItemIndex = ((RecyclerView.ViewHolder) listItem.getTag()).getBindingAdapterPosition();
        scaledUnitListViewModel.resolveScaledUnitId(listItemIndex, scaledUnitListNavigator::editScaledUnit);
    }

    private void onAddItem(View view) {
        scaledUnitListNavigator.addScaledUnit();
    }

    @Inject
    void setScaledUnitListNavigator(ScaledUnitListNavigator scaledUnitListNavigator) {
        this.scaledUnitListNavigator = scaledUnitListNavigator;
    }

    @Inject
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        scaledUnitListViewModel = viewModelProvider.get(ScaledUnitListViewModel.class);
    }
}
