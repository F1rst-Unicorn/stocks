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

package de.njsm.stocks.client.fragment.view;

import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import com.google.android.material.textfield.TextInputLayout;
import de.njsm.stocks.client.business.entities.ScaledUnitEditingFormData;
import de.njsm.stocks.client.business.entities.UnitForListing;
import de.njsm.stocks.client.business.entities.UnitForSelection;
import de.njsm.stocks.client.business.entities.conflict.ConflictData;
import de.njsm.stocks.client.presenter.UnitRenderStrategy;
import de.njsm.stocks.client.ui.R;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class ScaledUnitForm {

    private final ConflictTextField scaleField;

    private final ConflictSpinner unitField;

    private final ArrayAdapter<DataWrapper> unitAdapter;

    private final Function<Integer, String> dictionary;

    private boolean maySubmit;

    public ScaledUnitForm(View root, Function<Integer, String> dictionary) {
        this.scaleField = new ConflictTextField(root.findViewById(R.id.fragment_scaled_unit_form_scale));
        this.unitField = new ConflictSpinner(root.findViewById(R.id.fragment_scaled_unit_form_unit));
        this.dictionary = dictionary;

        scaleField.addNonEmptyValidator((a,b) -> onInputChanged(scaleField, a, b));
        scaleField.setEditorHint(R.string.hint_scale);
        scaleField.setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
        maySubmit = false;
        unitAdapter = new ArrayAdapter<>(root.getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1);
        unitField.setAdapter(unitAdapter);
    }

    public void showUnits(List<UnitForSelection> unitForSelections) {
        unitAdapter.clear();
        unitAdapter.addAll(unitForSelections.stream().map(DataWrapper::new).collect(toList()));
        unitAdapter.notifyDataSetChanged();
    }

    private void onInputChanged(ConflictTextField scaleField, TextInputLayout textInputLayout, Boolean isEmpty) {
        maySubmit = !isEmpty;
        if (isEmpty) {
            scaleField.setError(dictionary.apply(R.string.error_may_not_be_empty));
        } else {
            scaleField.setError(null);
        }
    }

    public BigDecimal getScale() {
        return new BigDecimal(scaleField.get());
    }

    public boolean maySubmit() {
        return maySubmit;
    }

    public void setError(@StringRes int id) {
        scaleField.setError(dictionary.apply(id));
    }

    public Optional<UnitForSelection> getUnit() {
        return Optional.ofNullable(unitField.<DataWrapper>getSelectedItem()).map(DataWrapper::delegate);
    }

    public void showScaledUnit(ScaledUnitEditingFormData scaledUnitEditingFormData) {
        scaleField.setEditorContent(scaledUnitEditingFormData.scale().toPlainString());
        showUnits(scaledUnitEditingFormData.availableUnits());
        unitField.setSelection(scaledUnitEditingFormData.currentUnitListPosition());
    }

    public void showScale(ConflictData<BigDecimal> scale) {
        scaleField.setEditorContent(scale.suggestedValue().toPlainString());
    }

    public void preSelectUnitPosition(int index) {
        unitField.setSelection(index);
    }

    public void hideUnit() {
        unitField.hide();
    }

    public void hideScale() {
        scaleField.hide();
    }

    public void showScaleConflict(ConflictData<BigDecimal> scale) {
        scaleField.showConflictInfo(scale, BigDecimal::toPlainString);
    }

    public void showUnitConflict(ConflictData<UnitForListing> unit) {
        unitField.showConflictInfo(unit, new UnitRenderStrategy()::render);
    }

    private static final class DataWrapper {

        private final UnitForSelection delegate;

        private DataWrapper(UnitForSelection delegate) {
            this.delegate = delegate;
        }

        private UnitForSelection delegate() {
            return delegate;
        }

        @NonNull
        @Override
        public String toString() {
            return delegate.name();
        }
    }
}
