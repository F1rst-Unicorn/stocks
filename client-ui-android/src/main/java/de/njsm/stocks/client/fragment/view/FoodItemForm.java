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
import android.widget.ArrayAdapter;
import de.njsm.stocks.client.business.entities.LocationForSelection;
import de.njsm.stocks.client.business.entities.ScaledUnitForSelection;
import de.njsm.stocks.client.presenter.UnitAmountRenderStrategy;
import de.njsm.stocks.client.ui.R;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class FoodItemForm {

    private final ConflictDate date;

    private final ConflictSpinner location;

    private final ArrayAdapter<EntityStringDisplayWrapper<LocationForSelection>> locationAdapter;

    private final ConflictSpinner unit;

    private final ArrayAdapter<EntityStringDisplayWrapper<ScaledUnitForSelection>> unitAdapter;

    public FoodItemForm(View root) {
        date = new ConflictDate(root.findViewById(R.id.fragment_food_item_form_date));
        location = new ConflictSpinner(root.findViewById(R.id.fragment_food_item_form_location));
        unit = new ConflictSpinner(root.findViewById(R.id.fragment_food_item_form_unit));

        locationAdapter = new ArrayAdapter<>(root.getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1);
        location.setAdapter(locationAdapter);

        unitAdapter = new ArrayAdapter<>(root.getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1);
        unit.setAdapter(unitAdapter);
    }

    public void showLocations(List<LocationForSelection> locations, int predictedLocationListPosition) {
        locationAdapter.clear();
        locationAdapter.addAll(locations.stream()
                .map(v -> new EntityStringDisplayWrapper<>(v, LocationForSelection::name))
                .collect(toList()));
        locationAdapter.notifyDataSetChanged();
        location.setSelection(predictedLocationListPosition);
    }

    public void showUnits(List<ScaledUnitForSelection> scaledUnits, int predictedScaledUnit) {
        unitAdapter.clear();
        UnitAmountRenderStrategy renderStrategy = new UnitAmountRenderStrategy();
        unitAdapter.addAll(scaledUnits.stream()
                .map(v -> new EntityStringDisplayWrapper<>(v, renderStrategy::render))
                .collect(toList()));
        unitAdapter.notifyDataSetChanged();
        unit.setSelection(predictedScaledUnit);
    }

    public void setPredictionDate(LocalDate predictedEatBy) {
        date.setSelection(predictedEatBy);
        date.setPredict(predictedEatBy);
    }

    public void setToday(LocalDate today) {
        date.setToday(today);
    }

    public LocalDate eatBy() {
        return date.get();
    }

    public LocationForSelection storedIn() {
        return location.<EntityStringDisplayWrapper<LocationForSelection>>getSelectedItem().delegate();
    }

    public ScaledUnitForSelection unit() {
        return unit.<EntityStringDisplayWrapper<ScaledUnitForSelection>>getSelectedItem().delegate();
    }
}
