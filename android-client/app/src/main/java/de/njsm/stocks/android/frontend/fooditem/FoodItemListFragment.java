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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.views.FoodItemView;
import de.njsm.stocks.android.frontend.InjectedFragment;
import de.njsm.stocks.android.frontend.interactor.FoodItemDeletionInteractor;

import java.util.List;

public class FoodItemListFragment extends InjectedFragment {

    private FoodItemViewModel viewModel;

    private FoodItemAdapter adapter;

    private FoodItemFragmentArgs input;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_food_item_list, container, false);

        assert getArguments() != null;
        input = FoodItemFragmentArgs.fromBundle(getArguments());

        result.findViewById(R.id.fragment_food_item_list_fab).setOnClickListener(this::addItem);
        RecyclerView list = result.findViewById(R.id.fragment_food_item_list_list);
        list.setLayoutManager(new LinearLayoutManager(requireActivity()));

        viewModel = getViewModelProvider().get(FoodItemViewModel.class);
        viewModel.initList(input.getFoodId());

        adapter = new FoodItemAdapter(getResources(), requireActivity().getTheme(),
                viewModel.getFoodItems(), this::editItem);
        viewModel.getFoodItems().observe(getViewLifecycleOwner(), v -> adapter.notifyDataSetChanged());
        list.setAdapter(adapter);

        FoodItemDeletionInteractor interactor = new FoodItemDeletionInteractor(
                this,
                i -> viewModel.deleteItem(i),
                i -> viewModel.getItem(i));
        addSwipeToDelete(list, viewModel.getFoodItems(), R.drawable.ic_local_dining_white_24dp, interactor::initiateDeletion);

        setHasOptionsMenu(true);
        return result;
    }

    private void addItem(View view) {
        FoodItemFragmentDirections.ActionNavFragmentFoodItemToNavFragmentAddFoodItem args =
                FoodItemFragmentDirections.actionNavFragmentFoodItemToNavFragmentAddFoodItem(input.getFoodId());
        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                .navigate(args);
    }

    private void editItem(View view) {
        FoodItemAdapter.ViewHolder holder = (FoodItemAdapter.ViewHolder) view.getTag();
        int position = holder.getAdapterPosition();
        List<FoodItemView> data = viewModel.getFoodItems().getValue();
        if (data != null) {
            int id = data.get(position).id;

            FoodItemFragmentDirections.ActionNavFragmentFoodItemToNavFragmentEditFoodItem args =
                    FoodItemFragmentDirections.actionNavFragmentFoodItemToNavFragmentEditFoodItem(id);
            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                    .navigate(args);
        }
    }
}
