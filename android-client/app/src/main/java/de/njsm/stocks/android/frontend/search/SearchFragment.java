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

package de.njsm.stocks.android.frontend.search;

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
import de.njsm.stocks.android.db.views.FoodSummaryView;
import de.njsm.stocks.android.frontend.InjectedFragment;
import de.njsm.stocks.android.frontend.interactor.FoodDeletionInteractor;
import de.njsm.stocks.android.frontend.interactor.FoodEditInteractor;
import de.njsm.stocks.android.frontend.interactor.FoodToBuyInteractor;

import java.util.List;

public class SearchFragment extends InjectedFragment {

    private AmountAdapter adapter;

    private SearchViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.template_swipe_list, container, false);
        assert getArguments() != null;
        SearchFragmentArgs input = SearchFragmentArgs.fromBundle(getArguments());

        RecyclerView list = result.findViewById(R.id.template_swipe_list_list);
        list.setLayoutManager(new LinearLayoutManager(requireActivity()));

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SearchViewModel.class);
        viewModel.setSearchTerm(input.getSearchTerm());

        FoodEditInteractor editor = new FoodEditInteractor(this,
                viewModel::renameFood,
                viewModel::getFood);

        adapter = new AmountAdapter(
                viewModel.getFoundData(),
                this::onClick,
                v -> editInternally(v,
                        viewModel.getFoundData(),
                        R.string.dialog_rename_food, editor::observeEditing));
        viewModel.getFoundData().observe(getViewLifecycleOwner(), v -> adapter.notifyDataSetChanged());
        list.setAdapter(adapter);

        FoodDeletionInteractor interactor = new FoodDeletionInteractor(
                this, result,
                i -> adapter.notifyItemChanged(i.getPosition()),
                viewModel::deleteFood,
                viewModel::getFood);

        FoodToBuyInteractor buyInteractor = new FoodToBuyInteractor(this,
                (f, s) -> {
                    adapter.notifyItemChanged(f.getPosition());
                    return viewModel.setToBuyStatus(f, s);
                },
                viewModel::getFood);

        addBidirectionalSwiper(list, viewModel.getFoundData(), R.drawable.ic_add_shopping_cart_white_24,
                interactor::initiateDeletion,
                v -> buyInteractor.observeEditing(v, true));

        initialiseSwipeRefresh(result, viewModelFactory);
        result.findViewById(R.id.template_swipe_list_fab).setVisibility(View.GONE);
        return result;
    }

    private void onClick(View view) {
        AmountAdapter.ViewHolder holder = (AmountAdapter.ViewHolder) view.getTag();
        int position = holder.getAdapterPosition();
        List<FoodSummaryView> list = viewModel.getFoundData().getValue();
        if (list != null) {
            int id = list.get(position).getId();
            SearchFragmentDirections.ActionNavFragmentSearchToNavFragmentFoodItem args =
                    SearchFragmentDirections.actionNavFragmentSearchToNavFragmentFoodItem(id);
            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                    .navigate(args);
        }
    }
}
