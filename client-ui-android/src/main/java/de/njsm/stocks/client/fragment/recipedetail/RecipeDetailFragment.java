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

package de.njsm.stocks.client.fragment.recipedetail;

import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.njsm.stocks.client.business.entities.RecipeForDetails;
import de.njsm.stocks.client.business.entities.RecipeItem;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.navigation.RecipeDetailNavigator;
import de.njsm.stocks.client.presenter.RecipeDetailViewModel;
import de.njsm.stocks.client.presenter.UnitAmountRenderStrategy;
import de.njsm.stocks.client.ui.R;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeDetailFragment extends BottomToolbarFragment implements MenuProvider {

    private RecipeDetailViewModel viewModel;

    private RecipeDetailNavigator navigator;

    private UnitAmountRenderStrategy renderStrategy;

    @NonNull
    @NotNull
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        View view = insertContent(inflater, root, R.layout.fragment_recipe_details);

        SwipeRefreshLayout swipeLayout = view.findViewById(R.id.fragment_recipe_details_swipe);
        swipeLayout.setOnRefreshListener(() -> {
            viewModel.synchronise();
            swipeLayout.setRefreshing(false);
        });

        viewModel.get(navigator.getRecipe(requireArguments())).observe(getViewLifecycleOwner(), this::onBindData);
        requireActivity().addMenuProvider(this, getViewLifecycleOwner());
        return root;
    }

    private void onBindData(RecipeForDetails recipeForDetails) {
        requireActivity().setTitle(recipeForDetails.name());
        TextView instructions = getView().findViewById(R.id.fragment_recipe_details_instructions);
        instructions.setText(recipeForDetails.instructions());

        TextView duration = getView().findViewById(R.id.fragment_recipe_details_duration);
        duration.setText(String.valueOf(recipeForDetails.duration().toMinutes()));

        renderRecipeItems(recipeForDetails.ingredients(), R.id.fragment_recipe_details_ingredient_list);
        renderRecipeItems(recipeForDetails.products(), R.id.fragment_recipe_details_product_list);
    }

    private void renderRecipeItems(List<? extends RecipeItem> list, @IdRes int targetViewId) {
        String content = list
                .stream()
                .map(v -> String.format(getString(R.string.dialog_recipe_item),
                        renderStrategy.render(v.neededAmount()),
                        v.foodName(),
                        renderStrategy.render(v.storedAmounts())))
                .collect(Collectors.joining("\n"));
        TextView targetView = getView().findViewById(targetViewId);
        targetView.setText(content);
    }

    @Override
    public void onCreateMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_recipe_details, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.menu_recipe_details_prepare) {
            navigator.prepare(navigator.getRecipe(requireArguments()));
            return true;
        } else if (menuItem.getItemId() == R.id.menu_recipe_details_edit) {
            navigator.edit(navigator.getRecipe(requireArguments()));
            return true;
        }
        return false;
    }

    @Inject
    @Override
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        viewModel = viewModelProvider.get(RecipeDetailViewModel.class);
    }

    @Inject
    void setNavigator(RecipeDetailNavigator navigator) {
        this.navigator = navigator;
    }

    @Inject
    void setRenderStrategy(UnitAmountRenderStrategy renderStrategy) {
        this.renderStrategy = renderStrategy;
    }
}
