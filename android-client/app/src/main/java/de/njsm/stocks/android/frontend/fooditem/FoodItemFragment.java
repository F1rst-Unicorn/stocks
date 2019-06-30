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

import android.content.Context;
import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.views.FoodItemView;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.eannumber.EanNumberViewModel;
import de.njsm.stocks.android.frontend.emptyfood.FoodViewModel;
import de.njsm.stocks.android.frontend.interactor.FoodItemDeletionInteractor;
import de.njsm.stocks.android.network.server.StatusCode;

import javax.inject.Inject;
import java.util.List;

public class FoodItemFragment extends BaseFragment {

    private FoodItemViewModel viewModel;

    private FoodViewModel foodViewModel;

    private EanNumberViewModel eanNumberViewModel;

    private ViewModelProvider.Factory viewModelFactory;

    private FoodItemAdapter adapter;

    private FoodItemFragmentArgs input;

    private LiveData<Food> selfFood;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.template_swipe_list, container, false);

        assert getArguments() != null;
        input = FoodItemFragmentArgs.fromBundle(getArguments());

        result.findViewById(R.id.template_swipe_list_fab).setOnClickListener(this::addItem);
        RecyclerView list = result.findViewById(R.id.template_swipe_list_list);
        list.setLayoutManager(new LinearLayoutManager(requireActivity()));

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FoodItemViewModel.class);
        eanNumberViewModel = ViewModelProviders.of(this, viewModelFactory).get(EanNumberViewModel.class);
        viewModel.init(input.getFoodId());
        foodViewModel = ViewModelProviders.of(this, viewModelFactory).get(FoodViewModel.class);
        selfFood = foodViewModel.getFood(input.getFoodId());
        selfFood.observe(this, u -> requireActivity().setTitle(u == null ? "" : u.name));
        selfFood.observe(this, u -> requireActivity().invalidateOptionsMenu());

        adapter = new FoodItemAdapter(getResources(), requireActivity().getTheme(),
                viewModel.getFoodItems(), this::editItem);
        viewModel.getFoodItems().observe(this, v -> adapter.notifyDataSetChanged());
        list.setAdapter(adapter);

        FoodItemDeletionInteractor interactor = new FoodItemDeletionInteractor(
                this, result,
                i -> adapter.notifyDataSetChanged(),
                i -> viewModel.deleteItem(i),
                i -> viewModel.getItem(i));
        addSwipeToDelete(list, viewModel.getFoodItems(), R.drawable.ic_local_dining_white_24dp, interactor::initiateDeletion);

        setHasOptionsMenu(true);
        initialiseSwipeRefresh(result, viewModelFactory);
        maybeAddEanCode(input.getEanNumber());
        return result;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_food_item_options, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.fragment_food_item_options_shopping);
        Food f = selfFood.getValue();
        if (f != null) {
            if (f.toBuy)
                item.setIcon(R.drawable.ic_remove_shopping_cart_white_24);
            else
                item.setIcon(R.drawable.ic_add_shopping_cart_white_24);
        }
    }

    private void maybeAddEanCode(String eanNumber) {
        if (eanNumber != null && ! eanNumber.isEmpty()) {
            eanNumberViewModel.addEanNumber(eanNumber, input.getFoodId())
                    .observe(this, this::maybeShowAddError);
        }
        getArguments().remove("eanNumber");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fragment_food_item_options_ean:
                FoodItemFragmentDirections.ActionNavFragmentFoodItemToNavFragmentEanNumber args =
                        FoodItemFragmentDirections.actionNavFragmentFoodItemToNavFragmentEanNumber(input.getFoodId());
                Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                        .navigate(args);
                break;
            case R.id.fragment_food_item_options_shopping:
                Food f = selfFood.getValue();
                if (f != null) {
                    LiveData<StatusCode> code = foodViewModel.setToBuyStatus(f, ! f.toBuy);
                    code.observe(this, c -> {
                        code.removeObservers(this);
                        requireActivity().invalidateOptionsMenu();
                    });
                }
                break;
        }
        return true;
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }
}
