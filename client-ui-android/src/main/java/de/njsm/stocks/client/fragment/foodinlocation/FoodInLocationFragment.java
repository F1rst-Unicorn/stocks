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

package de.njsm.stocks.client.fragment.foodinlocation;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.Localiser;
import de.njsm.stocks.client.business.entities.FoodForListing;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.Location;
import de.njsm.stocks.client.business.entities.LocationName;
import de.njsm.stocks.client.databind.ExpirationIconProvider;
import de.njsm.stocks.client.databind.FoodAdapter;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.fragment.listswipe.SwipeCallback;
import de.njsm.stocks.client.fragment.view.FoodOutlineViewHolder;
import de.njsm.stocks.client.fragment.view.TemplateSwipeList;
import de.njsm.stocks.client.navigation.FoodByLocationNavigator;
import de.njsm.stocks.client.presenter.FoodByLocationListViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;
import java.util.List;

public class FoodInLocationFragment extends BottomToolbarFragment implements MenuProvider {

    private FoodByLocationNavigator navigator;

    private FoodByLocationListViewModel viewModel;

    private TemplateSwipeList templateSwipeList;

    private FoodAdapter foodListAdapter;

    private Localiser localiser;

    private ExpirationIconProvider expirationIconProvider;

    private Id<Location> location;

    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        View swipeList = insertContent(inflater, root, R.layout.template_swipe_list);

        templateSwipeList = new TemplateSwipeList(swipeList);
        templateSwipeList.setLoading();

        location = navigator.getId(requireArguments());
        foodListAdapter = new FoodAdapter(this::onItemClicked, this::onItemLongClicked, expirationIconProvider, localiser);
        viewModel.getFood(location).observe(getViewLifecycleOwner(), this::onListDataReceived);
        viewModel.getLocation(location).observe(getViewLifecycleOwner(), this::setTitle);

        SwipeCallback callback = new SwipeCallback(
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_delete_white_24dp),
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_add_shopping_cart_white_24),
                new ColorDrawable(ContextCompat.getColor(requireActivity(), R.color.colorAccent)),
                this::onItemSwipedRight,
                this::onItemSwipedLeft
        );

        templateSwipeList.initialiseListWithSwiper(requireContext(), foodListAdapter, callback);
        templateSwipeList.bindFloatingActionButton(this::onAddItem);
        templateSwipeList.bindSwipeDown(this::onSwipeDown);

        requireActivity().addMenuProvider(this, getViewLifecycleOwner());

        return root;
    }

    private void onItemSwipedRight(int listItemIndex) {
        viewModel.delete(listItemIndex);
    }

    private void onItemSwipedLeft(int listItemIndex) {
        viewModel.putOnShoppingList(listItemIndex);
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

    private void onListDataReceived(List<FoodForListing> data) {
        if (data.isEmpty()) {
            templateSwipeList.setEmpty(R.string.hint_no_food_in_location);
        } else {
            templateSwipeList.setList();
        }
        foodListAdapter.setData(data);
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.history, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.menu_history) {
            navigator.showHistory(location);
            return true;
        }

        return false;
    }

    private void setTitle(LocationName title) {
        requireActivity().setTitle(title.name());
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
        viewModel = viewModelProvider.get(FoodByLocationListViewModel.class);
    }

    @Inject
    void setNavigator(FoodByLocationNavigator navigator) {
        this.navigator = navigator;
    }

    @Inject
    void setClock(Localiser localiser) {
        this.localiser = localiser;
    }

    @Inject
    void setExpirationIconProvider(ExpirationIconProvider expirationIconProvider) {
        this.expirationIconProvider = expirationIconProvider;
    }
}
