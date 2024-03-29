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

package de.njsm.stocks.client.fragment.unitlist;

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
import de.njsm.stocks.client.business.entities.UnitForListing;
import de.njsm.stocks.client.fragment.InjectableFragment;
import de.njsm.stocks.client.fragment.listswipe.SwipeCallback;
import de.njsm.stocks.client.fragment.view.TemplateSwipeList;
import de.njsm.stocks.client.navigation.UnitListNavigator;
import de.njsm.stocks.client.presenter.UnitListViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;
import java.util.List;

public class UnitListFragment extends InjectableFragment {

    private UnitListViewModel unitListViewModel;

    private UnitListNavigator unitListNavigator;

    private UnitAdapter unitListAdapter;

    private TemplateSwipeList templateSwipeList;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.template_swipe_list, container, false);
        templateSwipeList = new TemplateSwipeList(root);
        templateSwipeList.setLoading();
        templateSwipeList.disableSwipeRefresh();

        unitListAdapter = new UnitAdapter(this::onItemClicked);
        unitListViewModel.getUnits().observe(getViewLifecycleOwner(), this::onListDataReceived);

        SwipeCallback callback = new SwipeCallback(
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_delete_white_24dp),
                new ColorDrawable(ContextCompat.getColor(requireActivity(), R.color.colorAccent)),
                this::onItemSwipedRight
        );

        templateSwipeList.initialiseListWithSwiper(requireContext(), unitListAdapter, callback);
        templateSwipeList.bindFloatingActionButton(this::onAddItem);

        return root;
    }

    private void onListDataReceived(List<UnitForListing> unitForListings) {
        if (unitForListings.isEmpty()) {
            templateSwipeList.setEmpty(R.string.hint_no_units);
        } else {
            templateSwipeList.setList();
        }
        unitListAdapter.setData(unitForListings);
    }

    private void onItemClicked(View listItem) {
        int listItemIndex = ((RecyclerView.ViewHolder) listItem.getTag()).getBindingAdapterPosition();
        unitListViewModel.resolveUnitId(listItemIndex, unitListNavigator::editUnit);
    }

    private void onItemSwipedRight(int listItemIndex) {
        unitListViewModel.deleteUnit(listItemIndex);
    }

    private void onAddItem(View view) {
        unitListNavigator.addUnit();
    }

    @Inject
    void setUnitListNavigator(UnitListNavigator unitListNavigator) {
        this.unitListNavigator = unitListNavigator;
    }

    @Inject
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        unitListViewModel = viewModelProvider.get(UnitListViewModel.class);
    }
}
