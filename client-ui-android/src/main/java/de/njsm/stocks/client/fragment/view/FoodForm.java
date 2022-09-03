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
import com.google.android.material.textfield.TextInputLayout;
import de.njsm.stocks.client.business.entities.LocationForSelection;
import de.njsm.stocks.client.business.entities.ScaledUnitForListing;
import de.njsm.stocks.client.presenter.ScaledUnitRenderStrategy;
import de.njsm.stocks.client.ui.R;

import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static de.njsm.stocks.client.fragment.view.ViewUtility.stringFromForm;
import static java.util.stream.Collectors.toList;

public class FoodForm {

    private final ConflictTextField nameField;

    private final ConflictSwitch toBuyField;

    private final ConflictTextField expirationOffsetField;

    private final ConflictSpinner locationField;

    private final ArrayAdapter<EntityStringDisplayWrapper<LocationForSelection>> locationAdapter;

    private final ConflictSpinner storeUnitField;

    private final ArrayAdapter<EntityStringDisplayWrapper<ScaledUnitForListing>> unitAdapter;

    private final TextInputLayout descriptionField;

    private final Function<Integer, String> dictionary;

    private boolean maySubmit = false;

    private final EntityStringDisplayWrapper<LocationForSelection> locationSentinel;

    public FoodForm(View root, Function<Integer, String> dictionary) {
        this.nameField = new ConflictTextField(root.findViewById(R.id.fragment_food_form_name));
        this.toBuyField = new ConflictSwitch(root.findViewById(R.id.fragment_food_form_to_buy));
        this.expirationOffsetField = new ConflictTextField(root.findViewById(R.id.fragment_food_form_expiration_offset));
        this.locationField = new ConflictSpinner(root.findViewById(R.id.fragment_food_form_location));
        this.storeUnitField = new ConflictSpinner(root.findViewById(R.id.fragment_food_form_store_unit));
        this.descriptionField = root.findViewById(R.id.fragment_food_form_description);
        this.dictionary = dictionary;

        nameField.addNonEmptyValidator(this::onNameChanged);
        nameField.setEditorHint(R.string.hint_name);

        toBuyField.setText(dictionary.apply(R.string.hint_to_buy));
        toBuyField.setChecked(true);

        expirationOffsetField.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        expirationOffsetField.setEditorHint(R.string.hint_expiration_offset);
        expirationOffsetField.setEditorContent("0");

        locationAdapter = new ArrayAdapter<>(root.getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1);
        locationField.setAdapter(locationAdapter);
        locationSentinel = new EntityStringDisplayWrapper<>(LocationForSelection.create(-1, "---"), LocationForSelection::name);

        unitAdapter = new ArrayAdapter<>(root.getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1);
        storeUnitField.setAdapter(unitAdapter);
    }

    private void onNameChanged(TextInputLayout textInputLayout, boolean isEmpty) {
        maySubmit = !isEmpty;
        if (isEmpty)
            textInputLayout.setError(dictionary.apply(R.string.error_may_not_be_empty));
        else
            textInputLayout.setError(null);
    }

    public void showLocations(List<LocationForSelection> locationsForSelection) {
        locationAdapter.clear();
        locationAdapter.add(locationSentinel);
        locationAdapter.addAll(locationsForSelection.stream()
                .map(v -> new EntityStringDisplayWrapper<>(v, LocationForSelection::name))
                .collect(toList()));
        locationAdapter.notifyDataSetChanged();
    }

    public void showUnits(List<ScaledUnitForListing> unitsForSelection) {
        unitAdapter.clear();
        ScaledUnitRenderStrategy renderStrategy = new ScaledUnitRenderStrategy();
        unitAdapter.addAll(unitsForSelection.stream()
                .map(v -> new EntityStringDisplayWrapper<>(v, renderStrategy::render))
                .collect(toList()));
        unitAdapter.notifyDataSetChanged();
    }

    public boolean maySubmit() {
        return maySubmit;
    }

    public void showErrors() {
        nameField.setError(dictionary.apply(R.string.error_may_not_be_empty));
    }

    public String getName() {
        return nameField.get();
    }

    public boolean getToBuy() {
        return toBuyField.get();
    }

    public Period getExpirationOffset() {
        String raw = expirationOffsetField.get();
        String parseableInt = raw.isEmpty() ? "0" : raw;
        return Period.ofDays(Integer.parseInt(parseableInt));
    }

    public Optional<LocationForSelection> getLocation() {
        return Optional.ofNullable(
                        locationField.<EntityStringDisplayWrapper<LocationForSelection>>getSelectedItem())
                .flatMap(v -> {
                    if (v == locationSentinel) {
                        return Optional.empty();
                    } else {
                        return Optional.of(v);
                    }
                })
                .map(EntityStringDisplayWrapper::delegate);
    }

    public Optional<ScaledUnitForListing> getStoreUnit() {
        return Optional.ofNullable(
                        storeUnitField.<EntityStringDisplayWrapper<ScaledUnitForListing>>getSelectedItem())
                .map(EntityStringDisplayWrapper::delegate);
    }

    public String getDescription() {
        return stringFromForm(descriptionField);
    }
}
