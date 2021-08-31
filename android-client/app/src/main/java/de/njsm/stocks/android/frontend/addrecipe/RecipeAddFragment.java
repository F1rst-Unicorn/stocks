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
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.views.ScaledUnitView;
import de.njsm.stocks.android.error.TextResourceException;
import de.njsm.stocks.android.frontend.InjectedFragment;
import de.njsm.stocks.android.frontend.emptyfood.FoodViewModel;
import de.njsm.stocks.android.frontend.recipe.RecipeViewModel;
import de.njsm.stocks.android.frontend.units.ScaledUnitViewModel;
import de.njsm.stocks.common.api.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeAddFragment extends InjectedFragment {


    private MenuItem submitButton;

    private MenuItem indicator;

    private RecipeViewModel recipeViewModel;

    private FoodViewModel foodViewModel;

    private ScaledUnitViewModel scaledUnitViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View result = inflater.inflate(R.layout.fragment_recipe_add, container, false);

        foodViewModel = new ViewModelProvider(this, viewModelFactory).get(FoodViewModel.class);
        foodViewModel.initAllFood();
        scaledUnitViewModel = new ViewModelProvider(this, viewModelFactory).get(ScaledUnitViewModel.class);
        recipeViewModel = new ViewModelProvider(this, viewModelFactory).get(RecipeViewModel.class);

        result.findViewById(R.id.fragment_recipe_add_add_ingredient).setOnClickListener(this::addIngredientView);
        result.findViewById(R.id.fragment_recipe_add_add_product).setOnClickListener(this::addProductView);

        return result;
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

    private void startFormSubmission() {
        indicator.setVisible(true);
        submitButton.setVisible(false);

        try {
            FullRecipeForInsertion recipe = readFormData();
            LiveData<StatusCode> result = recipeViewModel.addRecipe(recipe);
            result.observe(this, this::maybeShowAddError);
            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                    .navigateUp();
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

        FullRecipeForInsertion.Builder builder = FullRecipeForInsertion.builder()
                .recipe(recipeForInsertion);

        LinearLayout ingredients = requireView().findViewById(R.id.fragment_recipe_add_ingredient_list);
        for (int i = 0; i < ingredients.getChildCount(); i++) {
            View ingredient = ingredients.getChildAt(i);
            builder.addIngredient(RecipeIngredientForInsertion.builder()
                    .amount(readInteger(ingredient, R.string.error_amount_invalid, R.id.item_scaled_food_amount))
                    .ingredient(readFood(ingredient))
                    .unit(readScaledUnit(ingredient))
                    .build());
        }

        LinearLayout products = requireView().findViewById(R.id.fragment_recipe_add_product_list);
        for (int i = 0; i < products.getChildCount(); i++) {
            View product = products.getChildAt(i);
            builder.addProduct(RecipeProductForInsertion.builder()
                    .amount(readInteger(product, R.string.error_amount_invalid, R.id.item_scaled_food_amount))
                    .product(readFood(product))
                    .unit(readScaledUnit(product))
                    .build());
        }

        return builder
                .build();
    }

    private int readInteger(View view, int errorStringId, int viewId) throws TextResourceException {
        try {
            return Integer.parseInt(getTextFieldContent(view, viewId));
        } catch (NumberFormatException e) {
            throw new TextResourceException(e, errorStringId);
        }
    }

    int readScaledUnit(View scaledFood) {
        Spinner unitField = scaledFood.findViewById(R.id.item_scaled_food_unit);
        int position = unitField.getSelectedItemPosition();
        List<ScaledUnitView> scaledUnits = scaledUnitViewModel.getUnits().getValue();
        if (scaledUnits != null && position != -1 && position < scaledUnits.size()) {
            return scaledUnits.get(position).id;
        } else {
            return 0;
        }
    }

    int readFood(View scaledFood) {
        Spinner foodField = scaledFood.findViewById(R.id.item_scaled_food_food);
        int position = foodField.getSelectedItemPosition();
        List<Food> scaledUnits = foodViewModel.getAllFood().getValue();
        if (scaledUnits != null && position != -1 && position < scaledUnits.size()) {
            return scaledUnits.get(position).id;
        } else {
            return 0;
        }
    }

    private void hideConflictLabels() {
        requireView().findViewById(R.id.fragment_recipe_add_name_conflict).setVisibility(View.GONE);
        requireView().findViewById(R.id.fragment_recipe_add_duration_conflict).setVisibility(View.GONE);
    }

    private void addIngredientView(View view) {
        addScaledFoodEntry(R.id.fragment_recipe_add_ingredient_list);
    }

    private void addProductView(View view) {
        addScaledFoodEntry(R.id.fragment_recipe_add_product_list);
    }

    private void addScaledFoodEntry(int rootView) {
        LinearLayout ingredientRootView = requireView().findViewById(rootView);
        View scaledFoodView = getLayoutInflater().inflate(R.layout.item_scaled_food, null);
        ingredientRootView.addView(scaledFoodView);
        scaledFoodView.findViewById(R.id.item_scaled_food_delete).setOnClickListener(v ->
                ingredientRootView.removeView(scaledFoodView));

        initialiseUnitSpinner(scaledFoodView);
        initialiseFoodSpinner(scaledFoodView);
    }

    private void initialiseUnitSpinner(View scaledFoodView) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(),
                R.layout.item_unit_spinner, R.id.item_unit_spinner_name,
                new ArrayList<>());
        Spinner unitSpinner = scaledFoodView.findViewById(R.id.item_scaled_food_unit);
        unitSpinner.setAdapter(adapter);
        scaledUnitViewModel.getUnits()
                .observe(getViewLifecycleOwner(), l -> {
            List<String> data = l.stream().map(ScaledUnitView::getPrettyName).collect(Collectors.toList());
            adapter.clear();
            adapter.addAll(data);
            adapter.notifyDataSetChanged();
        });
    }

    private void initialiseFoodSpinner(View scaledFoodView) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1,
                new ArrayList<>());
        Spinner unitSpinner = scaledFoodView.findViewById(R.id.item_scaled_food_food);
        unitSpinner.setAdapter(adapter);
        foodViewModel.getAllFood()
                .observe(getViewLifecycleOwner(), l -> {
            List<String> data = l.stream().map(Food::getName).collect(Collectors.toList());
            adapter.clear();
            adapter.addAll(data);
            adapter.notifyDataSetChanged();
        });
    }
}
