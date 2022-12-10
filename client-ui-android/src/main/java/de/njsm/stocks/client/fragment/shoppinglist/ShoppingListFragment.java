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

package de.njsm.stocks.client.fragment.shoppinglist;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.FoodWithAmountForListing;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.fragment.listswipe.SwipeCallback;
import de.njsm.stocks.client.fragment.searchedfood.FoodAmountViewHolder;
import de.njsm.stocks.client.fragment.view.TemplateSwipeList;
import de.njsm.stocks.client.navigation.ShoppingListNavigator;
import de.njsm.stocks.client.presenter.ShoppingListViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;
import java.util.List;

public class ShoppingListFragment extends BottomToolbarFragment {

    private ShoppingListNavigator navigator;

    private ShoppingListViewModel viewModel;

    private TemplateSwipeList templateSwipeList;

    private FoodAmountAdapter foodListAdapter;

    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        View swipeList = insertContent(inflater, root, R.layout.template_swipe_list);

        templateSwipeList = new TemplateSwipeList(swipeList);
        templateSwipeList.setLoading();

        foodListAdapter = new FoodAmountAdapter(this::onItemClicked, this::onItemLongClicked);
        viewModel.getFood().observe(getViewLifecycleOwner(), this::onListDataReceived);

        SwipeCallback callback = new SwipeCallback(
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_remove_shopping_cart_white_24),
                new ColorDrawable(ContextCompat.getColor(requireActivity(), R.color.colorAccent)),
                this::onItemSwipedRight
        );

        templateSwipeList.initialiseListWithSwiper(requireContext(), foodListAdapter, callback);
        templateSwipeList.hideFloatingActionButton();
        templateSwipeList.bindSwipeDown(this::onSwipeDown);

        return root;
    }

    private void onItemSwipedRight(int listItemIndex) {
        viewModel.removeFromShoppingList(listItemIndex);
    }

    private void onItemClicked(View listItem) {
        int listItemIndex = ((FoodAmountViewHolder) listItem.getTag()).getBindingAdapterPosition();
        viewModel.resolveId(listItemIndex, navigator::showFood);
    }

    private boolean onItemLongClicked(View listItem) {
        int listItemIndex = ((FoodAmountViewHolder) listItem.getTag()).getBindingAdapterPosition();
        viewModel.resolveId(listItemIndex, navigator::editFood);
        return true;
    }

    private void onListDataReceived(List<FoodWithAmountForListing> data) {
        if (data.isEmpty()) {
            templateSwipeList.setEmpty(R.string.hint_no_food_to_buy);
        } else {
            templateSwipeList.setList();
        }
        foodListAdapter.setData(data);
    }

    private void onSwipeDown() {
        viewModel.synchronise();
    }

    @Inject
    @Override
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        viewModel = viewModelProvider.get(ShoppingListViewModel.class);
    }

    @Inject
    void setNavigator(ShoppingListNavigator navigator) {
        this.navigator = navigator;
    }
}
