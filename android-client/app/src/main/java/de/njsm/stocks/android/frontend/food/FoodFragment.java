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

package de.njsm.stocks.android.frontend.food;

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
import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.views.FoodView;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.interactor.FoodDeletionInteractor;
import de.njsm.stocks.android.frontend.interactor.FoodEditInteractor;
import de.njsm.stocks.android.frontend.locations.LocationViewModel;

import javax.inject.Inject;
import java.util.List;

public class FoodFragment extends BaseFragment {

    private RecyclerView list;

    private ViewModelProvider.Factory viewModelFactory;

    private FoodToEatViewModel viewModel;

    private LiveData<List<FoodView>> data;

    private LocationViewModel locationViewModel;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.template_swipe_list, container, false);

        list = result.findViewById(R.id.template_swipe_list_list);
        list.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FoodToEatViewModel.class);
        locationViewModel = ViewModelProviders.of(this, viewModelFactory).get(LocationViewModel.class);

        if (getArguments() != null) {
            FoodFragmentArgs input = FoodFragmentArgs.fromBundle(getArguments());
            if (input.getLocation() != 0) {
                data = viewModel.getFoodByLocation(input.getLocation());
                locationViewModel.getLocation(input.getLocation()).observe(this,
                        d -> requireActivity().setTitle(d.name));
            } else {
                data = viewModel.getFoodToEat();
                requireActivity().setTitle(R.string.action_eat_next);
            }
        }

        FoodEditInteractor editor = new FoodEditInteractor(this,
                (f, s) -> viewModel.renameFood(f, s),
                viewModel::getFood);

        FoodAdapter adapter = new FoodAdapter(
                data,
                getResources(), requireActivity().getTheme(), this::onClick,
                v -> editInternally(v, viewModel.getFoodToEat(), R.string.dialog_rename_food,
                        (f,s) -> editor.observeEditing(f.mapToFood(), s))
        );
        data.observe(this, i -> adapter.notifyDataSetChanged());
        list.setAdapter(adapter);

        FoodDeletionInteractor interactor = new FoodDeletionInteractor(
                this, result,
                i -> adapter.notifyDataSetChanged(),
                i -> viewModel.deleteFood(i),
                i -> viewModel.getFood(i));
        addSwipeToDelete(list, data, v -> interactor.initiateDeletion(v.mapToFood()));

        result.findViewById(R.id.template_swipe_list_fab).setOnClickListener(v -> addFood(viewModel));
        initialiseSwipeRefresh(result, viewModelFactory);
        return result;
    }

    private void onClick(View view) {
        FoodAdapter.ViewHolder holder = (FoodAdapter.ViewHolder) view.getTag();
        int position = holder.getAdapterPosition();
        List<FoodView> data = this.data.getValue();
        if (data != null) {
            int id = data.get(position).id;
            FoodFragmentDirections.ActionNavFragmentFoodToNavFragmentFoodItem args =
                    FoodFragmentDirections.actionNavFragmentFoodToNavFragmentFoodItem(id);
            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                    .navigate(args);
        }
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }
}
