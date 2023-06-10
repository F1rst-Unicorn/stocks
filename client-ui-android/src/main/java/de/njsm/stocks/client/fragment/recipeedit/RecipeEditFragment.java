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

package de.njsm.stocks.client.fragment.recipeedit;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.Recipe;
import de.njsm.stocks.client.business.entities.RecipeEditBaseData;
import de.njsm.stocks.client.business.entities.RecipeEditForm;
import de.njsm.stocks.client.databind.RecipeForm;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.navigation.RecipeEditNavigator;
import de.njsm.stocks.client.presenter.RecipeEditViewModel;
import de.njsm.stocks.client.ui.R;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class RecipeEditFragment extends BottomToolbarFragment implements MenuProvider {

    private RecipeEditViewModel viewModel;

    private RecipeEditNavigator navigator;

    private RecipeIngredientEditFoodAdapter ingredientAdapter;

    private RecipeProductEditFoodAdapter productAdapter;

    private RecipeForm form;

    private Id<Recipe> recipeId;

    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        View view = insertContent(inflater, root, R.layout.fragment_recipe_form);
        form = new RecipeForm(view, this::getString);
        recipeId = navigator.getRecipe(requireArguments());

        viewModel.get(recipeId).observe(getViewLifecycleOwner(), data -> {
            ingredientAdapter = new RecipeIngredientEditFoodAdapter(data);
            productAdapter = new RecipeProductEditFoodAdapter(data);

            form.setName(data.recipe().name());
            form.setInstructions(data.recipe().instructions());
            form.setDuration(data.recipe().duration());
            form.setIngredients(ingredientAdapter, ingredientAdapter::delete);
            form.setProducts(productAdapter, productAdapter::delete);
            form.setOnAddIngredient(ingredientAdapter::add);
            form.setOnAddProduct(productAdapter::add);
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
        if (!form.maySubmit()) {
            form.showErrors();
            return true;
        }

        RecipeEditForm data = RecipeEditForm.create(
                RecipeEditBaseData.create(
                        recipeId.id(),
                        form.getName(),
                        form.getInstructions(),
                        form.getDuration()),
                ingredientAdapter.get(),
                productAdapter.get()
        );
        viewModel.submit(data);
        navigator.back();
        return true;
    }

    @Inject
    @Override
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        viewModel = viewModelProvider.get(RecipeEditViewModel.class);
    }

    @Inject
    void setNavigator(RecipeEditNavigator navigator) {
        this.navigator = navigator;
    }
}
