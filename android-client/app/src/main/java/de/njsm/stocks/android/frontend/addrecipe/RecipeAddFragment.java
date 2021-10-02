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

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.R;
import de.njsm.stocks.android.error.TextResourceException;
import de.njsm.stocks.android.frontend.InjectedFragment;
import de.njsm.stocks.android.frontend.emptyfood.FoodViewModel;
import de.njsm.stocks.android.frontend.recipe.RecipeViewModel;
import de.njsm.stocks.android.frontend.units.ScaledUnitViewModel;
import de.njsm.stocks.common.api.FullRecipeForInsertion;
import de.njsm.stocks.common.api.RecipeForInsertion;
import de.njsm.stocks.common.api.SelfValidating;
import de.njsm.stocks.common.api.StatusCode;

import java.time.Duration;

import static de.njsm.stocks.android.error.StatusCodeMessages.getAddErrorMessage;

public class RecipeAddFragment extends InjectedFragment {

    MenuItem submitButton;

    MenuItem indicator;

    RecipeViewModel recipeViewModel;

    FoodViewModel foodViewModel;

    ScaledUnitViewModel scaledUnitViewModel;

    RecipeIngredientForInsertionAdapter ingredientAdapter;

    RecipeProductForInsertionAdapter productAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View result = inflater.inflate(R.layout.fragment_recipe_add, container, false);

        foodViewModel = new ViewModelProvider(this, viewModelFactory).get(FoodViewModel.class);
        foodViewModel.initAllFood();
        scaledUnitViewModel = new ViewModelProvider(this, viewModelFactory).get(ScaledUnitViewModel.class);
        recipeViewModel = new ViewModelProvider(this, viewModelFactory).get(RecipeViewModel.class);
        scaledUnitViewModel.getUnits().observe(getViewLifecycleOwner(), v -> {});
        foodViewModel.getAllFood().observe(getViewLifecycleOwner(), v -> {});

        initialiseIngredientList(result);
        initialiseProductList(result);

        return result;
    }

    private void initialiseIngredientList(View result) {
        ingredientAdapter = new RecipeIngredientForInsertionAdapter(scaledUnitViewModel.getUnits(), foodViewModel.getAllFood());
        initialiseItemList(result, ingredientAdapter, R.id.fragment_recipe_add_ingredient_list, R.id.fragment_recipe_add_add_ingredient);
    }

    private void initialiseProductList(View result) {
        productAdapter = new RecipeProductForInsertionAdapter(scaledUnitViewModel.getUnits(), foodViewModel.getAllFood());
        initialiseItemList(result, productAdapter, R.id.fragment_recipe_add_product_list, R.id.fragment_recipe_add_add_product);
    }

    private <D extends SelfValidating, B extends SelfValidating.Builder<D>> void initialiseItemList(View result, ScaledFoodAdapter<D, B> adapter, int listId, int addButtonId) {
        RecyclerView productList = result.findViewById(listId);
        productList.setLayoutManager(new LinearLayoutManager(requireContext()));
        productList.setAdapter(adapter);
        DeletionSwiper callback = new DeletionSwiper(
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_delete_white_24dp),
                new ColorDrawable(ContextCompat.getColor(requireActivity(), R.color.colorAccent)),
                adapter::removeItem);
        new ItemTouchHelper(callback).attachToRecyclerView(productList);
        result.findViewById(addButtonId).setOnClickListener(v -> adapter.addItem());

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hideConflictLabels();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_food_edit_options, menu);
        indicator = menu.findItem(R.id.fragment_food_edit_options_indicator);
        indicator.setActionView(R.layout.template_progress_indicator);
        indicator.setVisible(false);
        submitButton = menu.findItem(R.id.fragment_food_edit_options_save);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.fragment_food_edit_options_save) {
            startFormSubmission();
        }

        return true;
    }

    void startFormSubmission() {
        indicator.setVisible(true);
        submitButton.setVisible(false);

        try {
            FullRecipeForInsertion recipe = readFormData();
            LiveData<StatusCode> result = recipeViewModel.addRecipe(recipe);
            result.observe(this, v -> {
                if (v == StatusCode.SUCCESS)
                    Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                            .navigateUp();
                else
                    showErrorMessage(requireActivity(), getAddErrorMessage(v));
            });
        } catch (TextResourceException e) {
            showErrorMessage(requireActivity(), e.getResourceId());
            indicator.setVisible(false);
            submitButton.setVisible(true);
        }
    }

    private FullRecipeForInsertion readFormData() throws TextResourceException {
        int durationInMinutes = readInteger(requireView(), R.string.error_duration_invalid, R.id.fragment_recipe_add_duration);

        RecipeForInsertion recipeForInsertion = RecipeForInsertion.builder()
                .name(getTextFieldContent(R.id.fragment_recipe_add_name))
                .duration(Duration.ofMinutes(durationInMinutes))
                .instructions(getTextFieldContent(R.id.fragment_recipe_add_instructions))
                .build();

        return FullRecipeForInsertion.builder()
                .recipe(recipeForInsertion)
                .ingredients(ingredientAdapter.getScaledFood())
                .products(productAdapter.getScaledFood())
                .build();
    }

    private int readInteger(View view, int errorStringId, int viewId) throws TextResourceException {
        try {
            return Integer.parseInt(getTextFieldContent(view, viewId));
        } catch (NumberFormatException e) {
            throw new TextResourceException(e, errorStringId);
        }
    }

    private void hideConflictLabels() {
        requireView().findViewById(R.id.fragment_recipe_add_name_conflict).setVisibility(View.GONE);
        requireView().findViewById(R.id.fragment_recipe_add_duration_conflict).setVisibility(View.GONE);
    }
}
