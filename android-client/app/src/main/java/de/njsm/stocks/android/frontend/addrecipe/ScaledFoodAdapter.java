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
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputLayout;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.views.ScaledUnitView;
import de.njsm.stocks.android.util.Logger;
import de.njsm.stocks.android.util.Utility;
import de.njsm.stocks.common.api.SelfValidating;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class ScaledFoodAdapter<D extends SelfValidating, B extends SelfValidating.Builder<D>,
                                        DN extends SelfValidating, BN extends SelfValidating.Builder<DN>> extends RecyclerView.Adapter<ScaledFoodAdapter<D, B, DN, BN>.ViewHolder> {

    private static final Logger LOG = new Logger(ScaledFoodAdapter.class);

    final List<BN> entities;

    private final LiveData<List<ScaledUnitView>> units;

    private final LiveData<List<Food>> food;

    public ScaledFoodAdapter(LiveData<List<ScaledUnitView>> units, LiveData<List<Food>> food) {
        this.entities = new ArrayList<>();
        this.units = units;
        this.food = food;
    }

    protected abstract class ViewHolder extends RecyclerView.ViewHolder {

        private final TextInputLayout amountField;

        private final Spinner unitSpinner;

        private final Spinner foodSpinner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            amountField = itemView.findViewById(R.id.item_scaled_food_amount);
            unitSpinner = itemView.findViewById(R.id.item_scaled_food_unit);
            foodSpinner = itemView.findViewById(R.id.item_scaled_food_food);

            amountField.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(@NonNull Editable s) {
                    try {
                        updateAmount(getAbsoluteAdapterPosition(), Integer.parseInt(amountField.getEditText().getText().toString()));
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

                    updateUnit(getAbsoluteAdapterPosition(), list.get(position).getId());
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

                    updateFood(getAbsoluteAdapterPosition(), list.get(position).getId());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        abstract void updateAmount(int position, int amount);

        abstract void updateUnit(int position, int unit);

        abstract void updateFood(int position, int food);

        void setAmount(int amount) {
            this.amountField.getEditText().setText(String.valueOf(amount));
        }

        void setUnit(int unit) {
            Utility.find(unit, units.getValue()).ifPresent(unitSpinner::setSelection);
        }

        void setFood(int food) {
            Utility.find(food, ScaledFoodAdapter.this.food.getValue()).ifPresent(foodSpinner::setSelection);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scaled_food, parent, false);
        ViewHolder result = newViewHolder(v);
        v.setTag(result);

        initialiseUnitSpinner(parent.getContext(), v);
        initialiseFoodSpinner(parent.getContext(), v);

        return result;
    }

    abstract ViewHolder newViewHolder(View v);

    public void addItem() {
        List<ScaledUnitView> units = this.units.getValue();
        if (units == null || units.isEmpty())
            return;

        List<Food> food = this.food.getValue();
        if (food == null || food.isEmpty())
            return;

        BN item = newBuilder(units, food);
        entities.add(item);
        notifyItemInserted(getItemCount() - 1);
    }

    abstract BN newBuilder(List<ScaledUnitView> units, List<Food> food);

    public void removeItem(int i) {
        entities.remove(i);
        notifyItemRemoved(i);
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    public List<DN> getScaledFood() {
        return entities.stream().map(SelfValidating.Builder::build).collect(Collectors.toList());
    }

    private void initialiseUnitSpinner(Context context, View scaledFoodView) {
        initialiseSpinner(context,
                scaledFoodView,
                units,
                ScaledUnitView::getPrettyName,
                R.id.item_scaled_food_unit,
                android.R.layout.simple_list_item_1,
                android.R.id.text1);
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
