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

package de.njsm.stocks.client.fragment.priceshow;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.client.fragment.InjectableFragment;
import de.njsm.stocks.client.navigation.PriceShowNavigator;
import de.njsm.stocks.client.presenter.PriceShowViewModel;
import de.njsm.stocks.client.ui.R;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class PriceShowFragment extends InjectableFragment implements MenuProvider {

    private PriceShowViewModel viewModel;

    private PriceShowNavigator navigator;

    private boolean toBuy;

    private IdImpl<Food> foodId;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_price_show, container, false);
        foodId = navigator.getFoodId(requireArguments()).toId();
        root.findViewById(R.id.fragmet_price_show_add).setOnClickListener(v -> navigator.addPrice(foodId));
        requireActivity().addMenuProvider(this, getViewLifecycleOwner());
        return root;
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_food_items, menu);
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
            navigator.editFood(foodId);
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

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        viewModel = viewModelProvider.get(PriceShowViewModel.class);
    }

    @Inject
    void setNavigator(PriceShowNavigator navigator) {
        this.navigator = navigator;
    }
}
