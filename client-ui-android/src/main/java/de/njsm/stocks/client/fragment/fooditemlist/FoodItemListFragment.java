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

package de.njsm.stocks.client.fragment.fooditemlist;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.client.business.Localiser;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.FoodItemsForListing;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.databind.ExpirationIconProvider;
import de.njsm.stocks.client.fragment.InjectableFragment;
import de.njsm.stocks.client.fragment.listswipe.SwipeCallback;
import de.njsm.stocks.client.fragment.view.TemplateSwipeList;
import de.njsm.stocks.client.navigation.FoodItemListNavigator;
import de.njsm.stocks.client.presenter.FoodItemListViewModel;
import de.njsm.stocks.client.ui.R;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class FoodItemListFragment extends InjectableFragment implements MenuProvider {

    private FoodItemListViewModel viewModel;

    private FoodItemListNavigator navigator;

    private FoodItemAdapter adapter;

    private TemplateSwipeList templateSwipeList;

    private ExpirationIconProvider expirationIconProvider;

    private Localiser localiser;

    private Id<Food> food;

    private boolean toBuy;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.template_swipe_list, container, false);
        templateSwipeList = new TemplateSwipeList(root);
        templateSwipeList.setLoading();
        templateSwipeList.disableSwipeRefresh();

        adapter = new FoodItemAdapter(this::onItemClicked, expirationIconProvider, localiser);
        food = navigator.getFoodId(requireArguments());
        viewModel.get(food).observe(getViewLifecycleOwner(), this::onListDataReceived);

        SwipeCallback callback = new SwipeCallback(
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_delete_white_24dp),
                new ColorDrawable(ContextCompat.getColor(requireActivity(), R.color.colorAccent)),
                this::onItemSwipedRight
        );

        templateSwipeList.initialiseListWithSwiper(requireContext(), adapter, callback);
        templateSwipeList.bindFloatingActionButton(this::onAddItem);
        requireActivity().addMenuProvider(this, getViewLifecycleOwner());
        return root;
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_food_items, menu);
    }

    @Override
    public void onPrepareMenu(@NonNull @NotNull Menu menu) {
        MenuItem item = menu.findItem(R.id.menu_food_items_shopping_list);
        if (toBuy) {
            item.setIcon(R.drawable.ic_remove_shopping_cart_white_24);
            item.setTitle(R.string.title_remove_from_cart);
        } else {
            item.setIcon(R.drawable.ic_add_shopping_cart_white_24);
            item.setTitle(R.string.title_add_to_cart);
        }
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_food_items_edit) {
            Id<Food> foodId = navigator.getFoodId(requireArguments());
            navigator.edit(foodId);
            return true;
        } else if (item.getItemId() == R.id.menu_food_items_ean_codes) {
            Id<Food> foodId = navigator.getFoodId(requireArguments());
            navigator.showEanNumbers(foodId);
            return true;
        } else if (item.getItemId() == R.id.menu_food_items_shopping_list) {
            Id<Food> foodId = navigator.getFoodId(requireArguments());
            viewModel.toggleShoppingFlag(foodId);
            return true;
        } else if (item.getItemId() == R.id.menu_food_items_history) {
            Id<Food> foodId = navigator.getFoodId(requireArguments());
            navigator.showHistory(foodId);
            return true;
        }
        return false;
    }

    private void onListDataReceived(FoodItemsForListing foodItemsForListing) {
        if (foodItemsForListing.foodItems().isEmpty()) {
            templateSwipeList.setEmpty(R.string.hint_no_food_items);
        } else {
            templateSwipeList.setList();
        }
        adapter.setData(foodItemsForListing.foodItems());
        requireActivity().setTitle(foodItemsForListing.foodName());
        toBuy = foodItemsForListing.toBuy();
        requireActivity().invalidateMenu();
    }

    private void onItemClicked(View listItem) {
        int listItemIndex = ((RecyclerView.ViewHolder) listItem.getTag()).getBindingAdapterPosition();
        viewModel.resolveId(listItemIndex, v -> navigator.edit(() -> v));
    }

    private void onItemSwipedRight(int listItemIndex) {
        viewModel.delete(listItemIndex);
    }

    private void onAddItem(View view) {
        navigator.add(food);
    }

    @Inject
    void setNavigator(FoodItemListNavigator navigator) {
        this.navigator = navigator;
    }

    @Inject
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        viewModel = viewModelProvider.get(FoodItemListViewModel.class);
    }

    @Inject
    void setExpirationIconProvider(ExpirationIconProvider expirationIconProvider) {
        this.expirationIconProvider = expirationIconProvider;
    }

    @Inject
    void setClock(Localiser localiser) {
        this.localiser = localiser;
    }
}
