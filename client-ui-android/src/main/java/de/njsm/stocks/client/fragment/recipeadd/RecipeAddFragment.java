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

package de.njsm.stocks.client.fragment.recipeadd;


import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.RecipeAddForm;
import de.njsm.stocks.client.databind.RecipeForm;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.navigation.Navigator;
import de.njsm.stocks.client.presenter.RecipeAddViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;
import java.io.Serializable;

public class RecipeAddFragment extends BottomToolbarFragment implements MenuProvider {

    private RecipeAddViewModel viewModel;

    private RecipeIngredientAddFoodAdapter ingredientAdapter;

    private RecipeProductFoodAddAdapter productAdapter;

    private Navigator navigator;

    private RecipeForm form;

    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        View view = insertContent(inflater, root, R.layout.fragment_recipe_form);
        form = new RecipeForm(view, this::getString);

        viewModel.get().observe(getViewLifecycleOwner(), data -> {
            if (data.availableFood().isEmpty() || data.availableUnits().isEmpty())
                return;

            ingredientAdapter = new RecipeIngredientAddFoodAdapter(data);
            productAdapter = new RecipeProductFoodAddAdapter(data);
            readSavedData(savedInstanceState);

            form.setIngredients(ingredientAdapter, ingredientAdapter::delete);
            form.setProducts(productAdapter, productAdapter::delete);
            form.setOnAddIngredient(ingredientAdapter::add);
            form.setOnAddProduct(productAdapter::add);
        });

        requireActivity().addMenuProvider(this, getViewLifecycleOwner());
        return root;
    }

    private void readSavedData(Bundle savedInstanceState) {
        if (savedInstanceState == null)
            return;

        Serializable serializable = savedInstanceState.getSerializable(RECIPE);
        if (serializable != null) {
            savedInstanceState.putSerializable(RECIPE, null);
            RecipeAddForm savedData = (RecipeAddForm) serializable;
            form.setName(savedData.name());
            form.setDuration(savedData.duration());
            form.setInstructions(savedData.instructions());
            ingredientAdapter.add(savedData.ingredients());
            productAdapter.add(savedData.products());
        }
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.check, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (!form.maySubmit()) {
            form.showErrors();
            return true;
        }

        RecipeAddForm data = RecipeAddForm.create(
                form.getName(),
                form.getInstructions(),
                form.getDuration(),
                ingredientAdapter.get(),
                productAdapter.get());
        viewModel.add(data);
        navigator.back();
        return true;
    }

    private static final String RECIPE = "RECIPE";

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(RECIPE, RecipeAddForm.create(
                form.getName(),
                form.getInstructions(),
                form.getDuration(),
                ingredientAdapter.get(),
                productAdapter.get()));
    }

    @Inject
    @Override
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        viewModel = viewModelProvider.get(RecipeAddViewModel.class);
    }

    @Inject
    void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}
