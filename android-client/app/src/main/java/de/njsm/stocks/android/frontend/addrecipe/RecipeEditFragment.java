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

package de.njsm.stocks.android.frontend.addrecipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.google.android.material.textfield.TextInputLayout;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Recipe;
import de.njsm.stocks.android.error.TextResourceException;
import de.njsm.stocks.android.frontend.recipedetail.RecipeIngredientViewModel;
import de.njsm.stocks.android.frontend.recipedetail.RecipeProductViewModel;
import de.njsm.stocks.common.api.*;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static de.njsm.stocks.android.error.StatusCodeMessages.getEditErrorMessage;

public class RecipeEditFragment extends RecipeAddFragment {

    RecipeIngredientForEditingAdapter ingredientAdapter;

    RecipeProductForEditingAdapter productAdapter;

    RecipeEditFragmentArgs input;

    RecipeIngredientViewModel recipeIngredientViewModel;

    RecipeProductViewModel recipeProductViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        assert getArguments() != null;
        input = RecipeEditFragmentArgs.fromBundle(getArguments());

        recipeIngredientViewModel = new ViewModelProvider(this, viewModelFactory).get(RecipeIngredientViewModel.class);
        recipeIngredientViewModel.init(input.getRecipeId());
        recipeProductViewModel = new ViewModelProvider(this, viewModelFactory).get(RecipeProductViewModel.class);
        recipeProductViewModel.init(input.getRecipeId());

        View result = super.onCreateView(inflater, container, savedInstanceState);
        recipeViewModel.init(input.getRecipeId());
        initFormContent(result);

        return result;
    }

    private void initFormContent(View view) {
        TextInputLayout name = view.findViewById(R.id.fragment_recipe_add_name);
        TextInputLayout instructions = view.findViewById(R.id.fragment_recipe_add_instructions);
        TextInputLayout duration = view.findViewById(R.id.fragment_recipe_add_duration);

        recipeViewModel.getRecipe().observe(getViewLifecycleOwner(), v -> {
            name.getEditText().setText(v.getName());
            instructions.getEditText().setText(v.getInstructions());
            duration.getEditText().setText(String.valueOf(v.getDuration().toMinutes()));
        });
    }

    @Override
    void initialiseIngredientList(View result) {
        recipeIngredientViewModel.getIngredients().observe(getViewLifecycleOwner(), v -> {
            List<RecipeIngredientForEditing.Builder> ingredients = v.stream()
                    .map(i -> RecipeIngredientForEditing.builder()
                                .id(i.getId())
                                .version(i.getVersion())
                                .amount(i.getAmount())
                                .ingredient(i.getIngredient())
                                .recipe(i.getRecipe())
                                .unit(i.getUnit())
                    )
                    .collect(Collectors.toList());
            ingredientAdapter = new RecipeIngredientForEditingAdapter(scaledUnitViewModel.getUnits(), foodViewModel.getAllFood(), ingredients, input.getRecipeId());
            initialiseItemList(result, ingredientAdapter, R.id.fragment_recipe_add_ingredient_list, R.id.fragment_recipe_add_add_ingredient);
        });
    }

    @Override
    void initialiseProductList(View result) {
        recipeProductViewModel.getProducts().observe(getViewLifecycleOwner(), v -> {
            List<RecipeProductForEditing.Builder> products = v.stream()
                    .map(i -> RecipeProductForEditing.builder()
                            .id(i.getId())
                            .version(i.getVersion())
                            .amount(i.getAmount())
                            .product(i.getProduct())
                            .recipe(i.getRecipe())
                            .unit(i.getUnit())
                    )
                    .collect(Collectors.toList());
            productAdapter = new RecipeProductForEditingAdapter(scaledUnitViewModel.getUnits(), foodViewModel.getAllFood(), products, input.getRecipeId());
            initialiseItemList(result, productAdapter, R.id.fragment_recipe_add_product_list, R.id.fragment_recipe_add_add_product);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.fragment_food_edit_options_save) {
            startFormSubmission();
        }

        return true;
    }

    @Override
    void submitForm() throws TextResourceException {
        FullRecipeForEditing recipe = readFormData();
        LiveData<StatusCode> result = recipeViewModel.editRecipe(recipe);
        result.observe(this, v -> {
            if (v == StatusCode.SUCCESS)
                Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                        .navigateUp();
            else
                showErrorMessage(requireActivity(), getEditErrorMessage(v));
        });
    }

    private FullRecipeForEditing readFormData() throws TextResourceException {
        int durationInMinutes = readInteger(requireView(), R.string.error_duration_invalid, R.id.fragment_recipe_add_duration);

        Recipe recipe = recipeViewModel.getRecipe().getValue();
        if (recipe == null)
            throw new TextResourceException(R.string.error_loading_recipe);

        RecipeForEditing recipeForEditing = RecipeForEditing.builder()
                .id(input.getRecipeId())
                .version(recipe.getVersion())
                .name(getTextFieldContent(R.id.fragment_recipe_add_name))
                .duration(Duration.ofMinutes(durationInMinutes))
                .instructions(getTextFieldContent(R.id.fragment_recipe_add_instructions))
                .build();

        return FullRecipeForEditing.builder()
                .recipe(recipeForEditing)
                .ingredients(ingredientAdapter.getIngredientsToEdit())
                .ingredientsToDelete(ingredientAdapter.getIngredientsToDelete())
                .ingredientsToInsert(ingredientAdapter.getIngredientsToInsert())
                .products(productAdapter.getProductsToEdit())
                .productsToDelete(productAdapter.getProductsToDelete())
                .productsToInsert(productAdapter.getProductsToInsert())
                .build();
    }
}
