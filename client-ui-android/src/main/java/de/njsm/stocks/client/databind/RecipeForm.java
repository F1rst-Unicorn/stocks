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

package de.njsm.stocks.client.databind;

import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputLayout;
import de.njsm.stocks.client.fragment.listswipe.SwipeCallback;
import de.njsm.stocks.client.fragment.view.ConflictTextField;
import de.njsm.stocks.client.ui.R;

import java.time.Duration;
import java.util.function.Function;

import static de.njsm.stocks.client.fragment.view.ViewUtility.setText;
import static de.njsm.stocks.client.fragment.view.ViewUtility.stringFromForm;

public class RecipeForm {

    private final View form;

    private final ImageButton ingredientAdd;

    private final ImageButton productAdd;

    private final ConflictTextField name;

    private final ConflictTextField duration;

    private final TextInputLayout instructions;

    private final RecyclerView ingredientList;

    private final RecyclerView productList;

    private final Function<Integer, String> dictionary;

    private boolean maySubmit;

    public RecipeForm(View form, Function<Integer, String> dictionary) {
        this.form = form;
        name = new ConflictTextField(form.findViewById(R.id.fragment_recipe_form_name));
        duration = new ConflictTextField(form.findViewById(R.id.fragment_recipe_form_duration));
        ingredientAdd = form.findViewById(R.id.fragment_recipe_form_add_ingredient);
        productAdd = form.findViewById(R.id.fragment_recipe_form_add_product);
        instructions = form.findViewById(R.id.fragment_recipe_form_instructions);
        ingredientList = form.findViewById(R.id.fragment_recipe_form_ingredient_list);
        productList = form.findViewById(R.id.fragment_recipe_form_product_list);
        this.dictionary = dictionary;

        duration.setEditorHint(R.string.hint_duration);
        duration.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        name.setEditorHint(R.string.hint_name);
        name.addNonEmptyValidator(this::onNameChanged);
        instructions.setHint(R.string.hint_instructions);
    }

    private void onNameChanged(TextInputLayout textInputLayout, Boolean isEmpty) {
        maySubmit = !isEmpty;
        if (isEmpty)
            textInputLayout.setError(dictionary.apply(R.string.error_may_not_be_empty));
        else
            textInputLayout.setError(null);
    }

    public void showErrors() {
        name.setError(dictionary.apply(R.string.error_may_not_be_empty));
    }

    public boolean maySubmit() {
        return maySubmit;
    }

    public String getName() {
        return name.get();
    }

    public Duration getDuration() {
        int value;
        try {
            value = Integer.parseInt(duration.get());
        } catch (NumberFormatException e) {
            value = 0;
        }
        return Duration.ofMinutes(value);
    }

    public String getInstructions() {
        return stringFromForm(instructions);
    }

    public void setOnAddIngredient(Runnable callback) {
        ingredientAdd.setOnClickListener(v -> callback.run());
    }

    public void setOnAddProduct(Runnable callback) {
        productAdd.setOnClickListener(v -> callback.run());
    }

    public void setName(String name) {
        this.name.setEditorContent(name);
    }

    public void setInstructions(String instructions) {
        setText(this.instructions, instructions);
    }

    public void setDuration(Duration duration) {
        this.duration.setEditorContent(String.valueOf(duration.toMinutes()));
    }

    public <T extends RecyclerView.ViewHolder> void setIngredients(RecyclerView.Adapter<T> ingredientAdapter, Consumer<Integer> swipeCallback) {
        configureRecyclerView(ingredientAdapter, swipeCallback, ingredientList);
    }

    public <T extends RecyclerView.ViewHolder> void setProducts(RecyclerView.Adapter<T> productAdapter, Consumer<Integer> swipeCallback) {
        configureRecyclerView(productAdapter, swipeCallback, productList);
    }

    private <T extends RecyclerView.ViewHolder> void configureRecyclerView(RecyclerView.Adapter<T> adapter, Consumer<Integer> swipeCallback, RecyclerView list) {
        list.setLayoutManager(new LinearLayoutManager(form.getContext()));
        list.setAdapter(adapter);
        SwipeCallback callback = new SwipeCallback(
                ContextCompat.getDrawable(form.getContext(), R.drawable.baseline_delete_black_24),
                new ColorDrawable(ContextCompat.getColor(form.getContext(), R.color.colorOnPrimary)),
                swipeCallback
        );
        new ItemTouchHelper(callback).attachToRecyclerView(list);
    }
}
