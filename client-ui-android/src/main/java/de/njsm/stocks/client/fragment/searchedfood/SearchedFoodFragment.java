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

package de.njsm.stocks.client.fragment.searchedfood;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.SearchedFoodForListing;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.fragment.listswipe.SwipeCallback;
import de.njsm.stocks.client.fragment.view.TemplateSwipeList;
import de.njsm.stocks.client.navigation.SearchedFoodNavigator;
import de.njsm.stocks.client.presenter.SearchedFoodViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;
import java.util.List;

public class SearchedFoodFragment extends BottomToolbarFragment {

    private SearchedFoodNavigator navigator;

    private SearchedFoodViewModel viewModel;

    private TemplateSwipeList templateSwipeList;

    private FoodAmountAdapter foodListAdapter;

    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        View swipeList = insertContent(inflater, root, R.layout.template_swipe_list);

        templateSwipeList = new TemplateSwipeList(swipeList);
        templateSwipeList.setLoading();

        String query = navigator.getQuery(requireArguments());
        requireActivity().setTitle(query);
        foodListAdapter = new FoodAmountAdapter(this::onItemClicked, this::onItemLongClicked);
        viewModel.getFood(query).observe(getViewLifecycleOwner(), this::onListDataReceived);

        SwipeCallback callback = new SwipeCallback(
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_delete_white_24dp),
                new ColorDrawable(ContextCompat.getColor(requireActivity(), R.color.colorAccent)),
                this::onItemSwipedRight
        );

        templateSwipeList.initialiseListWithSwiper(requireContext(), foodListAdapter, callback);
        templateSwipeList.hideFloatingActionButton();
        templateSwipeList.bindSwipeDown(this::onSwipeDown);

        return root;
    }

    private void onItemSwipedRight(int listItemIndex) {
        viewModel.delete(listItemIndex);
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

    private void onListDataReceived(List<SearchedFoodForListing> data) {
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

    @Inject
    @Override
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        viewModel = viewModelProvider.get(SearchedFoodViewModel.class);
    }

    @Inject
    void setNavigator(SearchedFoodNavigator navigator) {
        this.navigator = navigator;
    }
}
