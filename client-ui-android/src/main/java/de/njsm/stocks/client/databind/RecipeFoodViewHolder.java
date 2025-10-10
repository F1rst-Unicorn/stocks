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

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputLayout;
import de.njsm.stocks.client.business.entities.FoodForSelection;
import de.njsm.stocks.client.business.entities.ScaledUnitForSelection;
import de.njsm.stocks.client.fragment.view.EntityStringDisplayWrapper;
import de.njsm.stocks.client.fragment.view.ViewUtility;
import de.njsm.stocks.client.presenter.UnitAmountRenderStrategy;
import de.njsm.stocks.client.ui.R;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class RecipeFoodViewHolder extends RecyclerView.ViewHolder {

    private final TextInputLayout amount;

    private final TextInputLayout foodTextField;

    private final Spinner unitSpinner;

    private RecipeFoodDataChanged callback;

    public RecipeFoodViewHolder(@NonNull View itemView, List<FoodForSelection> availableFood, List<ScaledUnitForSelection> availableUnits) {
        super(itemView);
        amount = itemView.findViewById(R.id.item_recipe_food_amount);
        foodTextField = itemView.findViewById(R.id.item_recipe_food_food);
        unitSpinner = itemView.findViewById(R.id.item_recipe_food_unit);

        setupAutocompleteAdapter(itemView, availableFood, foodTextField, FoodForSelection::name);
        setupUnitAdapter(itemView, availableUnits, unitSpinner, new UnitAmountRenderStrategy()::render);
        ViewUtility.onEditorOf(this.amount, e -> e.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (callback != null) {
                    callback.onAmountChanged(
                            RecipeFoodViewHolder.this.getAbsoluteAdapterPosition(),
                            getParsedAmount());
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        }));
    }

    private void setupUnitAdapter(@NotNull View itemView, List<ScaledUnitForSelection> data, Spinner spinner, Function<ScaledUnitForSelection, String> displayMapper) {
        ArrayAdapter<EntityStringDisplayWrapper<ScaledUnitForSelection>> adapter = new ArrayAdapter<>(itemView.getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1);
        adapter.addAll(data.stream()
                .map(v -> new EntityStringDisplayWrapper<>(v, displayMapper))
                .collect(toList()));
        adapter.notifyDataSetChanged();
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (callback != null) {
                    EntityStringDisplayWrapper<ScaledUnitForSelection> item = adapter.getItem(position);
                    callback.onUnitChanged(
                            getAbsoluteAdapterPosition(),
                            item.delegate(),
                            position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupAutocompleteAdapter(@NotNull View itemView, List<FoodForSelection> data, TextInputLayout textField, Function<FoodForSelection, String> displayMapper) {
        ArrayAdapter<EntityStringDisplayWrapper<FoodForSelection>> adapter = new ArrayAdapter<>(itemView.getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1);
        List<EntityStringDisplayWrapper<FoodForSelection>> displayedFood = data.stream()
                .map(v -> new EntityStringDisplayWrapper<>(v, displayMapper))
                .collect(toList());
        adapter.addAll(displayedFood);
        adapter.notifyDataSetChanged();
        AutoCompleteTextView textView = (AutoCompleteTextView) textField.getEditText();
        textView.setAdapter(adapter);
        textView.setOnClickListener(v -> textView.setText(""));
        textView.setOnItemClickListener((parent, view, position, id) -> {
            if (callback != null) {
                EntityStringDisplayWrapper<FoodForSelection> item = adapter.getItem(position);
                callback.onFoodChanged(
                        getAbsoluteAdapterPosition(),
                        item.delegate());
            }
        });
        textView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (callback != null) {
                    EntityStringDisplayWrapper<FoodForSelection> item = adapter.getItem(position);
                    callback.onFoodChanged(
                            getAbsoluteAdapterPosition(),
                            item.delegate());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Set<String> validValues = displayedFood.stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
        textView.setValidator(new AutoCompleteTextView.Validator() {
            @Override
            public boolean isValid(CharSequence text) {
                return validValues.contains(text.toString());
            }

            @Override
            public CharSequence fixText(CharSequence invalidText) {
                return null;
            }
        });
    }

    private int getParsedAmount() {
        try {
            return Integer.parseInt(ViewUtility.stringFromForm(amount));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setAmount(int amount) {
        ViewUtility.setText(this.amount, amount);
    }

    public void setSelectedUnit(int position) {
        unitSpinner.setSelection(position);
    }

    public void setSelectedFood(int position) {
        AutoCompleteTextView textView = (AutoCompleteTextView) foodTextField.getEditText();
        textView.setText(textView.getAdapter().getItem(position).toString());
    }

    public void setCallback(RecipeFoodDataChanged callback) {
        this.callback = callback;
    }
}
