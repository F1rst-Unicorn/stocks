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
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.RecipeAddForm;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.navigation.Navigator;
import de.njsm.stocks.client.presenter.RecipeAddViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;

public class RecipeAddFragment extends BottomToolbarFragment {

    private RecipeAddViewModel viewModel;

    private RecipeIngredientFoodAdapter ingredientAdapter;

    private RecipeProductFoodAdapter productAdapter;

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

            ingredientAdapter = new RecipeIngredientFoodAdapter(data);
            productAdapter = new RecipeProductFoodAdapter(data);

            form.setIngredients(ingredientAdapter, ingredientAdapter::delete);
            form.setProducts(productAdapter, productAdapter::delete);
            form.setOnAddIngredient(ingredientAdapter::add);
            form.setOnAddProduct(productAdapter::add);
        });

        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.check, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (!form.maySubmit()) {
            form.showErrors();
            return true;
        }

        RecipeAddForm data = RecipeAddForm.create(
                form.getName(),
                form.getInstructions(),
                form.getDuration(),
                ingredientAdapter.get(),
                productAdapter.get()
        );
        viewModel.add(data);
        navigator.back();
        return true;
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
