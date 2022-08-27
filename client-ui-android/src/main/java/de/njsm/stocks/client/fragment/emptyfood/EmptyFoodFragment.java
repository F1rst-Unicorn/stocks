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

package de.njsm.stocks.client.fragment.emptyfood;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.EmptyFood;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.fragment.view.FoodOutlineViewHolder;
import de.njsm.stocks.client.fragment.view.TemplateSwipeList;
import de.njsm.stocks.client.navigation.EmptyFoodNavigator;
import de.njsm.stocks.client.presenter.EmptyFoodViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;
import java.util.List;

public class EmptyFoodFragment extends BottomToolbarFragment {

    private EmptyFoodNavigator navigator;

    private EmptyFoodViewModel viewModel;

    private TemplateSwipeList templateSwipeList;

    private FoodAdapter foodListAdapter;

    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        View swipeList = insertContent(inflater, root, R.layout.template_swipe_list);

        templateSwipeList = new TemplateSwipeList(swipeList);
        templateSwipeList.setLoading();

        foodListAdapter = new FoodAdapter(this::onItemClicked, this::onItemLongClicked);
        viewModel.getFood().observe(getViewLifecycleOwner(), this::onListDataReceived);

        templateSwipeList.initialiseList(requireContext(), foodListAdapter);
        templateSwipeList.bindFloatingActionButton(this::onAddItem);
        templateSwipeList.bindSwipeDown(this::onSwipeDown);

        return root;
    }

    private void onItemClicked(View listItem) {
        int listItemIndex = ((FoodOutlineViewHolder) listItem.getTag()).getBindingAdapterPosition();
        viewModel.resolveId(listItemIndex, navigator::showFood);
    }

    private boolean onItemLongClicked(View listItem) {
        int listItemIndex = ((FoodOutlineViewHolder) listItem.getTag()).getBindingAdapterPosition();
        viewModel.resolveId(listItemIndex, navigator::editFood);
        return true;
    }

    private void onListDataReceived(List<EmptyFood> data) {
        if (data.isEmpty()) {
            templateSwipeList.setEmpty(R.string.hint_no_food);
        } else {
            templateSwipeList.setList();
        }
        foodListAdapter.setData(data);
    }

    private void onSwipeDown() {
        viewModel.synchronise();
    }

    private void onAddItem(View view) {
        navigator.addFood();
    }

    @Inject
    @Override
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        viewModel = viewModelProvider.get(EmptyFoodViewModel.class);
    }

    @Inject
    void setNavigator(EmptyFoodNavigator navigator) {
        this.navigator = navigator;
    }
}
