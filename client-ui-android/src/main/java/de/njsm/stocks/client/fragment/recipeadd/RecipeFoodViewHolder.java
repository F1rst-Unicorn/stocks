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

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputLayout;
import de.njsm.stocks.client.fragment.view.ViewUtility;
import de.njsm.stocks.client.ui.R;

public class RecipeFoodViewHolder extends RecyclerView.ViewHolder {

    private final TextInputLayout amount;

    private final Spinner unitSpinner;

    private final Spinner foodSpinner;

    private RecipeFoodDataChanged callback;

    public RecipeFoodViewHolder(@NonNull View itemView) {
        super(itemView);
        amount = itemView.findViewById(R.id.item_recipe_food_amount);
        unitSpinner = itemView.findViewById(R.id.item_recipe_food_unit);
        foodSpinner = itemView.findViewById(R.id.item_recipe_food_food);
    }

    public void setAmount(int amount) {
        ViewUtility.setText(this.amount, amount);
        ViewUtility.onEditorOf(this.amount, e -> e.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                callback.update(RecipeFoodViewHolder.this.getAbsoluteAdapterPosition(),
                        Integer.parseInt(s.toString()),
                        foodSpinner.getSelectedItemPosition(),
                        unitSpinner.getSelectedItemPosition());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        }));
    }

    public void setUnit(SpinnerAdapter availableUnits, int position) {
        unitSpinner.setAdapter(availableUnits);
        unitSpinner.setSelection(position);
        unitSpinner.setOnItemClickListener((parent, view, position1, id) ->
                callback.update(getAbsoluteAdapterPosition(),
                        Integer.parseInt(ViewUtility.stringFromForm(amount)),
                        foodSpinner.getSelectedItemPosition(),
                        position1));
    }

    public void setFood(SpinnerAdapter availableFood, int position) {
        foodSpinner.setAdapter(availableFood);
        foodSpinner.setSelection(position);
        foodSpinner.setOnItemClickListener((parent, view, position1, id) ->
                callback.update(getAbsoluteAdapterPosition(),
                        Integer.parseInt(ViewUtility.stringFromForm(amount)),
                        position1,
                        unitSpinner.getSelectedItemPosition()));
    }

    public void setCallback(RecipeFoodDataChanged callback) {
        this.callback = callback;
    }
}
