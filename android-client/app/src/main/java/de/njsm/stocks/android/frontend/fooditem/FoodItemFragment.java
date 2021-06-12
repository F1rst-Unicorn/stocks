/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.android.frontend.fooditem;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.frontend.InjectedFragment;
import de.njsm.stocks.android.frontend.eannumber.EanNumberViewModel;
import de.njsm.stocks.android.frontend.emptyfood.FoodViewModel;
import de.njsm.stocks.android.network.server.StatusCode;

public class FoodItemFragment extends InjectedFragment implements SwipeListener {

    private FoodViewModel foodViewModel;

    private EanNumberViewModel eanNumberViewModel;

    private FoodItemFragmentArgs input;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_food_item, container, false);

        assert getArguments() != null;
        input = FoodItemFragmentArgs.fromBundle(getArguments());

        eanNumberViewModel = ViewModelProviders.of(this, viewModelFactory).get(EanNumberViewModel.class);
        foodViewModel = ViewModelProviders.of(this, viewModelFactory).get(FoodViewModel.class);
        foodViewModel.initFood(input.getFoodId());
        foodViewModel.getFood().observe(getViewLifecycleOwner(), u -> requireActivity().setTitle(u == null ? "" : u.name));
        foodViewModel.getFood().observe(getViewLifecycleOwner(), u -> requireActivity().invalidateOptionsMenu());

        ViewPager2 viewPager = result.findViewById(R.id.fragment_food_item_pager);
        viewPager.setAdapter(new TabAdapter(this, input));
        viewPager.setUserInputEnabled(false);

        TabLayout tabLayout = result.findViewById(R.id.fragment_food_item_tabs);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    int iconId;
                    if (position == 0) {
                        iconId = R.drawable.baseline_room_service_black_24;
                    } else {
                        iconId = R.drawable.baseline_insert_chart_black_24;
                    }
                    tab.setIcon(iconId);
                }
        ).attach();

        setHasOptionsMenu(true);
        maybeAddEanCode(input.getEanNumber());
        initialiseSwipeRefresh(result, R.id.fragment_food_item_swipe, viewModelFactory);
        return result;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_food_item_options, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.fragment_food_item_options_shopping);
        Food f = foodViewModel.getFood().getValue();
        if (f != null) {
            if (f.toBuy) {
                item.setIcon(R.drawable.ic_remove_shopping_cart_white_24);
                item.setTitle(R.string.title_remove_from_cart);
            } else {
                item.setIcon(R.drawable.ic_add_shopping_cart_white_24);
                item.setTitle(R.string.title_add_to_cart);
            }
        }
    }

    private void maybeAddEanCode(String eanNumber) {
        if (eanNumber != null && !eanNumber.isEmpty()) {
            eanNumberViewModel.addEanNumber(eanNumber, input.getFoodId())
                    .observe(getViewLifecycleOwner(), this::maybeShowAddError);
        }
        getArguments().remove("eanNumber");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.fragment_food_item_options_ean) {
            FoodItemFragmentDirections.ActionNavFragmentFoodItemToNavFragmentEanNumber args =
                    FoodItemFragmentDirections.actionNavFragmentFoodItemToNavFragmentEanNumber(input.getFoodId());
            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                    .navigate(args);
        } else if (item.getItemId() == R.id.fragment_food_item_options_shopping){
            Food f = foodViewModel.getFood().getValue();
            if (f != null) {
                LiveData<StatusCode> code = foodViewModel.setToBuyStatus(f, !f.toBuy);
                code.observe(this, c -> {
                    code.removeObservers(this);
                    requireActivity().invalidateOptionsMenu();
                });
            }
        } else if (item.getItemId() == R.id.fragment_food_item_options_events){
            goToEvents();
        } else if (item.getItemId() == R.id.fragment_food_item_options_edit){
            goToEditor();
        }

        return true;
    }

    private void goToEditor() {
        foodViewModel.getFood().observe(getViewLifecycleOwner(), f -> {
            FoodItemFragmentDirections.ActionNavFragmentFoodItemToNavFragmentFoodEdit args =
                    FoodItemFragmentDirections.actionNavFragmentFoodItemToNavFragmentFoodEdit(
                            f.id
                    );
            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                    .navigate(args);
        });
    }

    private void goToEvents() {
        FoodItemFragmentDirections.ActionNavFragmentFoodItemToNavFragmentFoodItemHistory args =
                FoodItemFragmentDirections.actionNavFragmentFoodItemToNavFragmentFoodItemHistory(input.getFoodId());
        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                .navigate(args);
    }

    public void setEnabled(boolean value) {
        getView().findViewById(R.id.fragment_food_item_swipe).setEnabled(value);
    }
}
