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

package de.njsm.stocks.android.frontend.emptyfood;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.frontend.InjectedFragment;
import de.njsm.stocks.android.frontend.interactor.FoodDeletionInteractor;
import de.njsm.stocks.android.frontend.interactor.FoodEditInteractor;
import de.njsm.stocks.android.frontend.interactor.FoodToBuyInteractor;

import java.util.List;

public class EmptyFoodFragment extends InjectedFragment {

    FoodAdapter adapter;

    EmptyFoodViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.template_swipe_list, container, false);

        RecyclerView list = result.findViewById(R.id.template_swipe_list_list);
        list.setLayoutManager(new LinearLayoutManager(requireActivity()));

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(EmptyFoodViewModel.class);

        FoodEditInteractor editor = new FoodEditInteractor(this,
                (f, s) -> viewModel.renameFood(f, s),
                viewModel::getFood);
        adapter = new FoodAdapter(
                viewModel.getEmptyFood(),
                this::onListItemClicked,
                v -> editInternally(v, viewModel.getEmptyFood(), R.string.dialog_rename_food,
                editor::observeEditing));
        viewModel.getEmptyFood().observe(getViewLifecycleOwner(), u -> adapter.notifyDataSetChanged());
        list.setAdapter(adapter);

        FoodDeletionInteractor deleter = new FoodDeletionInteractor(this, result,
                i -> adapter.notifyItemChanged(i.getPosition()),
                i -> viewModel.deleteFood(i),
                viewModel::getFood);

        FoodToBuyInteractor buyInteractor = new FoodToBuyInteractor(this,
                (f, s) -> {
                    adapter.notifyItemChanged(f.getPosition());
                    return viewModel.setToBuyStatus(f, s);
                },
                viewModel::getFood);

        addBidirectionalSwiper(list, viewModel.getEmptyFood(), R.drawable.ic_add_shopping_cart_white_24,
                deleter::initiateDeletion,
                v -> buyInteractor.observeEditing(v, true));

        initialiseSwipeRefresh(result, viewModelFactory);
        result.findViewById(R.id.template_swipe_list_fab).setOnClickListener(v -> this.addFood(viewModel));
        return result;
    }

    private void onListItemClicked(View view) {
        RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) view.getTag();
        int position = holder.getAdapterPosition();
        List<Food> data = viewModel.getEmptyFood().getValue();
        if (data != null) {
            int id = data.get(position).id;
            EmptyFoodFragmentDirections.ActionNavFragmentEmptyFoodToNavFragmentFoodItem args =
                    EmptyFoodFragmentDirections.actionNavFragmentEmptyFoodToNavFragmentFoodItem(id);
            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                    .navigate(args);
        }
    }
}
