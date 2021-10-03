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

package de.njsm.stocks.android.frontend.units;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Unit;
import de.njsm.stocks.android.db.views.ScaledUnitView;
import de.njsm.stocks.android.frontend.InjectedFragment;
import de.njsm.stocks.android.frontend.interactor.DeletionInteractor;
import de.njsm.stocks.android.frontend.interactor.Editor;
import de.njsm.stocks.android.frontend.util.NonEmptyValidator;
import de.njsm.stocks.common.api.StatusCode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ScaledUnitFragment extends InjectedFragment implements Editor<ScaledUnitView> {

    ScaledUnitViewModel viewModel;

    UnitViewModel unitViewModel;

    RecyclerView.Adapter<ScaledUnitAdapter.ViewHolder> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_units, container, false);

        result.findViewById(R.id.fragment_units_fab).setOnClickListener(this::addUnit);
        RecyclerView list = result.findViewById(R.id.fragment_units_list);
        list.setLayoutManager(new LinearLayoutManager(requireActivity()));

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ScaledUnitViewModel.class);
        unitViewModel = ViewModelProviders.of(this, viewModelFactory).get(UnitViewModel.class);

        adapter = new ScaledUnitAdapter(viewModel.getUnits(),
                v -> initiateEditing(v, viewModel.getUnits(), R.string.dialog_edit),
                this::doNothing);
        viewModel.getUnits().observe(getViewLifecycleOwner(), u -> adapter.notifyDataSetChanged());
        list.setAdapter(adapter);

        DeletionInteractor<ScaledUnitView> deletionInteractor = new DeletionInteractor<>(this, viewModel::delete);
        addSwipeToDelete(list, viewModel.getUnits(), deletionInteractor::initiateDeletion);

        return result;
    }

    private void addUnit(View view) {
        View form = getEditLayout(null, getLayoutInflater());

        new AlertDialog.Builder(requireActivity())
                .setTitle(getResources().getString(R.string.dialog_new_unit))
                .setView(form)
                .setPositiveButton(getResources().getString(R.string.dialog_ok), (dialog, whichButton) -> {
                    String scale = ((EditText) form.findViewById(R.id.form_scaled_unit_scale)).getText().toString();
                    Spinner spinner = form.findViewById(R.id.form_scaled_unit_unit);

                    int selected = spinner.getSelectedItemPosition();
                    List<Unit> units = unitViewModel.getUnits().getValue();
                    if (units != null) {
                        Unit unit = units.get(selected);
                        LiveData<StatusCode> result = viewModel.add(unit.id, scale);
                        result.observe(this, this::maybeShowAddError);
                    }
                })
                .show();
    }

    @Override
    public LiveData<StatusCode> edit(ScaledUnitView item, DialogInterface dialog, View view) {
        String scale = ((EditText) view.findViewById(R.id.form_scaled_unit_scale)).getText().toString();
        Spinner spinner = view.findViewById(R.id.form_scaled_unit_unit);
        int selected = spinner.getSelectedItemPosition();
        List<Unit> units = unitViewModel.getUnits().getValue();
        if (units != null) {
            Unit unit = units.get(selected);
            return viewModel.edit(item, unit.id, scale);
        } else {
            MutableLiveData<StatusCode> data = new MutableLiveData<>();
            data.setValue(StatusCode.SUCCESS);
            return data;
        }
    }

    @Override
    public View getEditLayout(ScaledUnitView item, LayoutInflater layoutInflater) {
        View result = layoutInflater.inflate(R.layout.form_scaled_unit, null);
        EditText scaleField = result.findViewById(R.id.form_scaled_unit_scale);

        if (item != null)
            scaleField.setText(String.format("%s", item.getScale()));

        scaleField.addTextChangedListener(new NonEmptyValidator(scaleField, this::showEmptyInputError));
        Spinner unitField = result.findViewById(R.id.form_scaled_unit_unit);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(),
                R.layout.item_unit_spinner, R.id.item_unit_spinner_name,
                new ArrayList<>());
        unitField.setAdapter(adapter);
        unitViewModel.getUnits().observe(getViewLifecycleOwner(), v -> {
            if (v == null)
                return;

            List<String> data = v.stream().map(i -> i.name).collect(Collectors.toList());
            adapter.clear();
            adapter.addAll(data);
            adapter.notifyDataSetChanged();

            if (item != null) {
                int selectionIndex = v.indexOf(item.getUnitEntity());
                if (selectionIndex != -1)
                    unitField.setSelection(selectionIndex);
            }
        });

        return result;
    }

    @Override
    public void treatStatusCode(ScaledUnitView item, StatusCode statusCode) {
        maybeShowEditError(statusCode);
    }
}
