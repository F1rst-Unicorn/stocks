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

package de.njsm.stocks.client.fragment.fooditemtabs;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.tabs.TabLayout;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.fragment.fooditemlist.FoodItemListFragment;
import de.njsm.stocks.client.fragment.unittabs.TabsFragment;
import de.njsm.stocks.client.navigation.FoodItemTabsNavigator;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Supplier;

public class FoodItemTabsFragment extends TabsFragment {

    private FoodItemTabsNavigator navigator;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View result = super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        return result;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_food_items, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_food_items_edit) {
            Id<Food> foodId = navigator.get(requireArguments());
            navigator.editFood(foodId);
            return true;
        } else if (item.getItemId() == R.id.menu_food_items_ean_codes) {
            Id<Food> foodId = navigator.get(requireArguments());
            navigator.showEanNumbers(foodId);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void setIcon(TabLayout.Tab tab, int position) {
        int iconId;
        if (position == 0) {
            iconId = R.drawable.baseline_room_service_black_24;
        } else {
            iconId = R.drawable.baseline_insert_chart_black_24;
        }
        tab.setIcon(iconId);
    }

    @Override
    public List<Supplier<? extends Fragment>> fragmentFactories() {
        return List.of(
                () -> {
                    Fragment result = new FoodItemListFragment();
                    result.setArguments(requireArguments());
                    return result;
                },
                () -> {
                    Fragment result = new FoodItemListFragment();
                    result.setArguments(requireArguments());
                    return result;
                }
        );
    }

    @Inject
    void setNavigator(FoodItemTabsNavigator navigator) {
        this.navigator = navigator;
    }
}
