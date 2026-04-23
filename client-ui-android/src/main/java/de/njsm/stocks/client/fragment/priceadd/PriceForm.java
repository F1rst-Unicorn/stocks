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

package de.njsm.stocks.client.fragment.priceadd;

import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;
import androidx.annotation.StringRes;
import com.google.android.material.textfield.TextInputLayout;
import de.njsm.stocks.client.business.entities.GroceryStoreForSelection;
import de.njsm.stocks.client.business.entities.ScaledUnitForSelection;
import de.njsm.stocks.client.fragment.view.ConflictSpinner;
import de.njsm.stocks.client.fragment.view.ConflictTextField;
import de.njsm.stocks.client.fragment.view.EntityStringDisplayWrapper;
import de.njsm.stocks.client.presenter.UnitAmountRenderStrategy;
import de.njsm.stocks.client.ui.R;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class PriceForm {

    private final DatePicker datePicker;

    private final TimePicker timePicker;

    private final ConflictTextField price;

    private final ConflictTextField scale;

    private final ConflictSpinner scaledUnit;

    private final ArrayAdapter<EntityStringDisplayWrapper<ScaledUnitForSelection>> unitAdapter;

    private final ConflictSpinner groceryStore;

    private final ArrayAdapter<EntityStringDisplayWrapper<GroceryStoreForSelection>> groceryStoreAdapter;

    private final Function<Integer, String> dictionary;

    private final Set<ConflictTextField> invalidFields;

    public PriceForm(View root, Function<Integer, String> dictionary) {
        this.datePicker = root.findViewById(R.id.fragment_price_form_valid_date);
        this.timePicker = root.findViewById(R.id.fragment_price_form_valid_time);
        this.price = new ConflictTextField(root.findViewById(R.id.fragment_price_form_price));
        this.scale = new ConflictTextField(root.findViewById(R.id.fragment_price_form_scale));
        this.scaledUnit = new ConflictSpinner(root.findViewById(R.id.fragment_price_form_scaled_unit));
        this.groceryStore = new ConflictSpinner(root.findViewById(R.id.fragment_price_form_grocery_store));
        this.dictionary = dictionary;
        invalidFields = new HashSet<>();
        invalidFields.add(price);
        unitAdapter = new ArrayAdapter<>(root.getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1);
        scaledUnit.setAdapter(unitAdapter);
        groceryStoreAdapter = new ArrayAdapter<>(root.getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1);
        groceryStore.setAdapter(groceryStoreAdapter);

        timePicker.setIs24HourView(true);
        price.addNonEmptyValidator((a,b) -> onInputChanged(price, a, b));
        price.setEditorHint(R.string.hint_price);
        price.setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
        scale.addNonEmptyValidator((a,b) -> onInputChanged(scale, a, b));
        scale.setEditorHint(R.string.hint_scale);
        scale.setEditorContent("1");
        scale.setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
    }

    private void onInputChanged(ConflictTextField conflictTextField, TextInputLayout textInputLayout, boolean isEmpty) {
        if (isEmpty) {
            invalidFields.add(conflictTextField);
            textInputLayout.setError(dictionary.apply(R.string.error_may_not_be_empty));
        } else {
            invalidFields.remove(conflictTextField);
            textInputLayout.setError(null);
        }
    }

    public boolean maySubmit() {
        return invalidFields.isEmpty();
    }

    public void setError(@StringRes int text) {
        for (ConflictTextField invalidField : invalidFields)
            invalidField.setError(dictionary.apply(text));
    }

    public LocalDate getDate() {
        return LocalDate.of(
                datePicker.getYear(),
                datePicker.getMonth()+1,
                datePicker.getDayOfMonth());
    }

    public LocalTime getTime() {
        return LocalTime.of(timePicker.getHour(), timePicker.getMinute());
    }

    public BigDecimal getPrice() {
        return new BigDecimal(price.get());
    }

    public BigDecimal getScale() {
        return new BigDecimal(scale.get());
    }

    public Optional<ScaledUnitForSelection> getUnit() {
        return Optional.ofNullable(
                        scaledUnit.<EntityStringDisplayWrapper<ScaledUnitForSelection>>getSelectedItem())
                .map(EntityStringDisplayWrapper::delegate);
    }

    public void showUnits(List<ScaledUnitForSelection> unitsForSelection) {
        unitAdapter.clear();
        UnitAmountRenderStrategy renderStrategy = new UnitAmountRenderStrategy();
        unitAdapter.addAll(unitsForSelection.stream()
                .map(v -> new EntityStringDisplayWrapper<>(v, renderStrategy::render))
                .collect(toList()));
        unitAdapter.notifyDataSetChanged();
    }

    public Optional<GroceryStoreForSelection> getGroceryStore() {
        return Optional.ofNullable(
                        groceryStore.<EntityStringDisplayWrapper<GroceryStoreForSelection>>getSelectedItem())
                .map(EntityStringDisplayWrapper::delegate);
    }

    public void showGroceryStores(List<GroceryStoreForSelection> groceryStoresForSelection) {
        groceryStoreAdapter.clear();
        UnitAmountRenderStrategy renderStrategy = new UnitAmountRenderStrategy();
        groceryStoreAdapter.addAll(groceryStoresForSelection.stream()
                .map(v -> new EntityStringDisplayWrapper<>(v, GroceryStoreForSelection::name))
                .collect(toList()));
        groceryStoreAdapter.notifyDataSetChanged();
    }
}
