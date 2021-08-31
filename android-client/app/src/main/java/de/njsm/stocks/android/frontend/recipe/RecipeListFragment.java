/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2021  The stocks developers
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

package de.njsm.stocks.android.frontend.recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.R;
import de.njsm.stocks.android.frontend.InjectedFragment;

public class RecipeListFragment extends InjectedFragment {

    private RecyclerView.Adapter<RecipeAdapter.ViewHolder> adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.template_swipe_list, container, false);

        result.findViewById(R.id.template_swipe_list_fab).setOnClickListener(this::goToRecipeForm);
        RecyclerView list = result.findViewById(R.id.template_swipe_list_list);
        list.setLayoutManager(new LinearLayoutManager(requireActivity()));

        RecipeViewModel viewModel = getViewModelProvider().get(RecipeViewModel.class);

        adapter = new RecipeAdapter(viewModel.getRecipes(),
                this::doNothing,
                this::doNothing);
        viewModel.getRecipes().observe(getViewLifecycleOwner(), u -> adapter.notifyDataSetChanged());
        list.setAdapter(adapter);

        initialiseSwipeRefresh(result, viewModelFactory);
        return result;
    }

    private void goToRecipeForm(View view) {
        NavDirections args = RecipeListFragmentDirections.actionNavFragmentRecipesToNavFragmentAddRecipe();
        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                .navigate(args);
    }
}
