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

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputLayout;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.views.ScaledUnitView;
import de.njsm.stocks.android.util.Logger;
import de.njsm.stocks.android.util.Utility;
import de.njsm.stocks.common.api.RecipeIngredientForInsertion;
import de.njsm.stocks.common.api.SelfValidating;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ScaledFoodAdapter extends RecyclerView.Adapter<ScaledFoodAdapter.ViewHolder> {

    private static final Logger LOG = new Logger(ScaledFoodAdapter.class);

    private final List<RecipeIngredientForInsertion.Builder> ingredients;

    private final LiveData<List<ScaledUnitView>> units;

    private final LiveData<List<Food>> food;

    public ScaledFoodAdapter(LiveData<List<ScaledUnitView>> units, LiveData<List<Food>> food) {
        this.ingredients = new ArrayList<>();
        this.units = units;
        this.food = food;
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        private final TextInputLayout amount;

        private final Spinner unitSpinner;

        private final Spinner foodSpinner;

        private RecipeIngredientForInsertion.Builder data;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            amount = itemView.findViewById(R.id.item_scaled_food_amount);
            unitSpinner = itemView.findViewById(R.id.item_scaled_food_unit);
            foodSpinner = itemView.findViewById(R.id.item_scaled_food_food);

            amount.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(@NonNull Editable s) {
                    try {
                        data.amount(Integer.parseInt(amount.getEditText().getText().toString()));
                    } catch (NumberFormatException e) {
                        LOG.w("recipe amount failed to parse: " + e.getMessage());
                    }
                }
            });

            unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    List<ScaledUnitView> list = units.getValue();
                    if (list == null)
                        return;

                    data.unit(list.get(position).getId());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            foodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    List<Food> list = ScaledFoodAdapter.this.food.getValue();
                    if (list == null)
                        return;

                    data.ingredient(list.get(position).getId());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        public void bindData(RecipeIngredientForInsertion.Builder data) {
            this.data = data;
            RecipeIngredientForInsertion currentState = data.build();
            amount.getEditText().setText(String.valueOf(currentState.amount()));
            Utility.find(currentState.unit(), units.getValue()).ifPresent(unitSpinner::setSelection);
            Utility.find(currentState.ingredient(), ScaledFoodAdapter.this.food.getValue()).ifPresent(foodSpinner::setSelection);
        }

    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecipeIngredientForInsertion.Builder item = ingredients.get(position);
        holder.bindData(item);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scaled_food, parent, false);
        ViewHolder result = new ViewHolder(v);
        v.setTag(result);

        initialiseUnitSpinner(parent.getContext(), v);
        initialiseFoodSpinner(parent.getContext(), v);

        return result;
    }

    public void addItem() {
        List<ScaledUnitView> units = this.units.getValue();
        if (units == null || units.isEmpty())
            return;

        List<Food> food = this.food.getValue();
        if (food == null || food.isEmpty())
            return;

        RecipeIngredientForInsertion.Builder item = RecipeIngredientForInsertion.builder()
                .amount(1)
                .unit(units.get(0).getId())
                .ingredient(food.get(0).getId());
        ingredients.add(item);
        notifyItemInserted(ingredients.size() - 1);
    }

    public void removeItem(int i) {
        ingredients.remove(i);
        notifyItemRemoved(i);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public List<RecipeIngredientForInsertion> getIngredients() {
        return ingredients.stream().map(SelfValidating.Builder::build).collect(Collectors.toList());
    }

    private void initialiseUnitSpinner(Context context, View scaledFoodView) {
        initialiseSpinner(context,
                scaledFoodView,
                units,
                ScaledUnitView::getPrettyName,
                R.id.item_scaled_food_unit,
                R.layout.item_unit_spinner,
                R.id.item_unit_spinner_name);
    }

    private void initialiseFoodSpinner(Context context, View scaledFoodView) {
        initialiseSpinner(context,
                scaledFoodView,
                food,
                Food::getName,
                R.id.item_scaled_food_food,
                android.R.layout.simple_list_item_1,
                android.R.id.text1);
    }

    private <T> void initialiseSpinner(Context context,
                                       View scaledFoodView,
                                       LiveData<List<T>> liveData,
                                       Function<T, String> stringMapper,
                                       int spinnerViewId,
                                       int itemLayout,
                                       int layoutStringField) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                itemLayout, layoutStringField,
                new ArrayList<>());
        Spinner unitSpinner = scaledFoodView.findViewById(spinnerViewId);
        unitSpinner.setAdapter(adapter);
        liveData.observeForever(l -> {
                    List<String> data = l.stream().map(stringMapper).collect(Collectors.toList());
                    adapter.clear();
                    adapter.addAll(data);
                    adapter.notifyDataSetChanged();
                });
    }
}
