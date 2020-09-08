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

package de.njsm.stocks.android.frontend.shoppinglist;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.views.FoodWithLatestItemView;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.interactor.FoodEditInteractor;
import de.njsm.stocks.android.frontend.interactor.FoodToBuyInteractor;
import de.njsm.stocks.android.frontend.search.AmountAdapter;

public class ShoppingListFragment extends BaseFragment {

    private ViewModelProvider.Factory viewModelFactory;

    private FoodToBuyViewModel viewModel;

    private LiveData<List<FoodWithLatestItemView>> data;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.template_swipe_list, container, false);

        RecyclerView list = result.findViewById(R.id.template_swipe_list_list);
        list.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FoodToBuyViewModel.class);
        data = viewModel.getFoodToBuy();

        FoodEditInteractor editor = new FoodEditInteractor(this,
                viewModel::renameFood,
                viewModel::getFood);
        AmountAdapter adapter = new AmountAdapter(
                data,
                this::onClick,
                v -> editInternally(v, viewModel.getFoodToBuy(), R.string.dialog_rename_food, (f,s) -> editor.observeEditing(f.mapToFood(), s)));
        data.observe(this, i -> adapter.notifyDataSetChanged());
        list.setAdapter(adapter);

        FoodToBuyInteractor buyInteractor = new FoodToBuyInteractor(this,
                (f, s) -> {
                    adapter.notifyItemRemoved(f.getPosition());
                    return viewModel.setToBuyStatus(f, s);
                },
                viewModel::getFood);
        addSwipeToDelete(list, data, R.drawable.ic_remove_shopping_cart_white_24, v -> buyInteractor.observeEditing(v.mapToFood(), false));

        result.findViewById(R.id.template_swipe_list_fab).setVisibility(View.GONE);
        initialiseSwipeRefresh(result, viewModelFactory);
        return result;
    }

    private void onClick(View view) {
        AmountAdapter.ViewHolder holder = (AmountAdapter.ViewHolder) view.getTag();
        int position = holder.getAdapterPosition();
        List<FoodWithLatestItemView> data = this.data.getValue();
        if (data != null) {
            int id = data.get(position).id;
            ShoppingListFragmentDirections.ActionNavFragmentShoppingListToNavFragmentFoodItem args =
                    ShoppingListFragmentDirections.actionNavFragmentShoppingListToNavFragmentFoodItem(id);
            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                    .navigate(args);
        }
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }
}
