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

package de.njsm.stocks.client.fragment.recipecook;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.client.business.entities.Recipe;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.navigation.RecipeCookNavigator;
import de.njsm.stocks.client.presenter.RecipeCookViewModel;
import de.njsm.stocks.client.ui.R;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class RecipeCookFragment extends BottomToolbarFragment implements MenuProvider {

    private RecipeCookViewModel viewModel;

    private RecipeCookNavigator navigator;

    private RecipeCookForm form;

    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        View view = insertContent(inflater, root, R.layout.fragment_recipe_cook);
        form = new RecipeCookForm(view, viewModel::putFoodToBuy);
        IdImpl<Recipe> recipeId = navigator.getRecipe(requireArguments());
        requireActivity().setTitle(R.string.title_cook_recipe);

        viewModel.get(recipeId).observe(getViewLifecycleOwner(), data -> {
            var mergedRecipe = data.mergeFrom(form.getCurrentIngredients(), form.getCurrentProducts());
            form.setIngredients(mergedRecipe.ingredients());
            form.setProducts(mergedRecipe.products());
        });

        requireActivity().addMenuProvider(this, getViewLifecycleOwner());
        return root;
    }


    @Override
    public void onCreateMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.check, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull @NotNull MenuItem menuItem) {
        navigator.back();
        return true;
    }

    @Inject
    @Override
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        viewModel = viewModelProvider.get(RecipeCookViewModel.class);
    }

    @Inject
    void setNavigator(RecipeCookNavigator navigator) {
        this.navigator = navigator;
    }
}
