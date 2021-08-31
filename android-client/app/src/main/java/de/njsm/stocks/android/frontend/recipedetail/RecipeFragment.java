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

package de.njsm.stocks.android.frontend.recipedetail;

import android.icu.text.MeasureFormat;
import android.icu.util.Measure;
import android.icu.util.MeasureUnit;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.views.ScaledFood;
import de.njsm.stocks.android.frontend.InjectedFragment;
import de.njsm.stocks.android.frontend.recipe.RecipeViewModel;

import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

import static android.text.format.DateUtils.*;

public class RecipeFragment extends InjectedFragment {

    private RecipeViewModel recipeViewModel;

    private RecipeIngredientViewModel recipeIngredientViewModel;

    private RecipeProductViewModel recipeProductViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_recipe, container, false);

        RecipeFragmentArgs input = RecipeFragmentArgs.fromBundle(getArguments());

        recipeViewModel = getViewModelProvider().get(RecipeViewModel.class);
        recipeViewModel.init(input.getRecipeId());
        recipeIngredientViewModel = getViewModelProvider().get(RecipeIngredientViewModel.class);
        recipeIngredientViewModel.init(input.getRecipeId());
        recipeProductViewModel = getViewModelProvider().get(RecipeProductViewModel.class);
        recipeProductViewModel.init(input.getRecipeId());

        recipeViewModel.getRecipe().observe(getViewLifecycleOwner(), v -> requireActivity().setTitle(v.getName()));
        recipeViewModel.getRecipe().observe(getViewLifecycleOwner(), v ->
            ((TextView) result.findViewById(R.id.fragment_recipe_instructions)).setText(v.getInstructions()));
        recipeViewModel.getRecipe().observe(getViewLifecycleOwner(), v ->
            ((TextView) result.findViewById(R.id.fragment_recipe_duration)).setText(
                    formatDuration(
                            v.getDuration().toMillis())));

        return result;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recipeIngredientViewModel.getIngredients().observe(getViewLifecycleOwner(), this::showIngredients);
        recipeProductViewModel.getProducts().observe(getViewLifecycleOwner(), this::showProducts);
    }

    private void showIngredients(List<ScaledFood> scaledFoods) {
        if (scaledFoods.isEmpty()) {
            requireView().findViewById(R.id.fragment_recipe_ingredient_list).setVisibility(View.GONE);
            requireView().findViewById(R.id.fragment_recipe_title_ingredients).setVisibility(View.GONE);
        } else {
            setScaledFoodList(scaledFoods, R.id.fragment_recipe_ingredient_list);
        }
    }

    private void showProducts(List<ScaledFood> scaledFoods) {
        if (scaledFoods.isEmpty()) {
            requireView().findViewById(R.id.fragment_recipe_product_list).setVisibility(View.GONE);
            requireView().findViewById(R.id.fragment_recipe_title_products).setVisibility(View.GONE);
        } else {
            setScaledFoodList(scaledFoods, R.id.fragment_recipe_product_list);
        }
    }

    private void setScaledFoodList(List<ScaledFood> scaledFoods, int viewId) {
        TextView text = requireView().findViewById(viewId);
        StringJoiner joiner = new StringJoiner("\n");
        scaledFoods.stream()
                .map(ScaledFood::getPrettyString)
                .forEach(joiner::add);
        text.setText(joiner.toString());
    }

    /**
     * See android.text.format.DateUtils.formatDuration()
     */
    public static CharSequence formatDuration(long millis) {
        final MeasureFormat.FormatWidth width = MeasureFormat.FormatWidth.WIDE;
        final MeasureFormat formatter = MeasureFormat.getInstance(Locale.getDefault(), width);
        if (millis >= HOUR_IN_MILLIS) {
            final int hours = (int) ((millis + 1800000) / HOUR_IN_MILLIS);
            return formatter.format(new Measure(hours, MeasureUnit.HOUR));
        } else if (millis >= MINUTE_IN_MILLIS) {
            final int minutes = (int) ((millis + 30000) / MINUTE_IN_MILLIS);
            return formatter.format(new Measure(minutes, MeasureUnit.MINUTE));
        } else {
            final int seconds = (int) ((millis + 500) / SECOND_IN_MILLIS);
            return formatter.format(new Measure(seconds, MeasureUnit.SECOND));
        }
    }
}
