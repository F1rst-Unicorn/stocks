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
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.navigation.RecipeCookNavigator;
import de.njsm.stocks.client.presenter.RecipeCookViewModel;
import de.njsm.stocks.client.ui.R;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Collection;
import java.util.stream.Collectors;

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
            RecipeCookingFormData mergedRecipe;
            if (savedInstanceState != null) {
                mergedRecipe = readSavedData(savedInstanceState, data);
            } else {
                mergedRecipe = data;
            }

            mergedRecipe = mergedRecipe.mergeFrom(form.getCurrentIngredients(), form.getCurrentProducts());
            form.setIngredients(mergedRecipe.ingredients());
            form.setProducts(mergedRecipe.products());
        });

        requireActivity().addMenuProvider(this, getViewLifecycleOwner());
        return root;
    }

    private static RecipeCookingFormData readSavedData(
            @NotNull Bundle savedInstanceState,
            RecipeCookingFormData data) {
        Serializable serializable = savedInstanceState.getSerializable(RECIPE);
        if (serializable != null) {
            savedInstanceState.putSerializable(RECIPE, null);
            RecipeCookingFormData savedData = (RecipeCookingFormData) serializable;
            return data.mergeFrom(savedData.ingredients(), savedData.products());
        } else {
            return data;
        }
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.check, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        var ingredientsToConsume = form.getCurrentIngredients()
                .stream()
                .map(RecipeCookingFormDataIngredient::toConsumptions)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        var productsToProduce = form.getCurrentProducts()
                .stream()
                .map(RecipeCookingFormDataProduct::toProductions)
                .collect(Collectors.toList());
        viewModel.cookRecipe(RecipeCookingForm.create(ingredientsToConsume, productsToProduce));

        navigator.back();
        return true;
    }

    private static final String RECIPE = "RECIPE";

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(RECIPE, RecipeCookingFormData.create(
                "",
                form.getCurrentIngredients(),
                form.getCurrentProducts()));
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
