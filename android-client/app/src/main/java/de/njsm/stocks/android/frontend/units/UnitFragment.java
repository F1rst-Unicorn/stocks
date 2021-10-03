/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.android.frontend.units;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Unit;
import de.njsm.stocks.android.frontend.InjectedFragment;
import de.njsm.stocks.android.frontend.interactor.DeletionInteractor;
import de.njsm.stocks.android.frontend.interactor.Editor;
import de.njsm.stocks.android.frontend.util.NonEmptyValidator;
import de.njsm.stocks.common.api.StatusCode;

public class UnitFragment extends InjectedFragment implements Editor<Unit> {

    UnitViewModel viewModel;

    RecyclerView.Adapter<UnitAdapter.ViewHolder> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_units, container, false);

        result.findViewById(R.id.fragment_units_fab).setOnClickListener(this::addUnit);
        RecyclerView list = result.findViewById(R.id.fragment_units_list);
        list.setLayoutManager(new LinearLayoutManager(requireActivity()));

        viewModel = new ViewModelProvider(this, viewModelFactory).get(UnitViewModel.class);

        adapter = new UnitAdapter(viewModel.getUnits(),
                v -> initiateEditing(v, viewModel.getUnits(), R.string.dialog_edit),
                this::doNothing);
        viewModel.getUnits().observe(getViewLifecycleOwner(), u -> adapter.notifyDataSetChanged());
        list.setAdapter(adapter);

        DeletionInteractor<Unit> deletionInteractor = new DeletionInteractor<>(this, viewModel::delete);
        addSwipeToDelete(list, viewModel.getUnits(), deletionInteractor::initiateDeletion);

        return result;
    }

    private void addUnit(View view) {
        View form = getEditingLayout(getLayoutInflater());

        new AlertDialog.Builder(requireActivity())
                .setTitle(getResources().getString(R.string.dialog_new_unit))
                .setView(form)
                .setPositiveButton(getResources().getString(R.string.dialog_ok), (dialog, whichButton) -> {
                    String name = ((EditText) form.findViewById(R.id.form_unit_name)).getText().toString().trim();
                    String abbreviation = ((EditText) form.findViewById(R.id.form_unit_abbreviation)).getText().toString().trim();
                    LiveData<StatusCode> result = viewModel.add(name, abbreviation);
                    result.observe(this, this::maybeShowAddError);
                })
                .show();
    }

    @Override
    public LiveData<StatusCode> edit(Unit item, DialogInterface dialog, View view) {
        String newName = ((EditText) view.findViewById(R.id.form_unit_name)).getText().toString().trim();
        String newAbbreviation = ((EditText) view.findViewById(R.id.form_unit_abbreviation)).getText().toString().trim();
        return viewModel.edit(item, newName, newAbbreviation);
    }

    public View getEditingLayout(LayoutInflater layoutInflater) {
        View result = layoutInflater.inflate(R.layout.form_unit, null);
        EditText nameField = result.findViewById(R.id.form_unit_name);
        nameField.addTextChangedListener(new NonEmptyValidator(nameField, this::showEmptyInputError));
        EditText abbreviationField = result.findViewById(R.id.form_unit_abbreviation);
        abbreviationField.addTextChangedListener(new NonEmptyValidator(abbreviationField, this::showEmptyInputError));
        return result;
    }

    @Override
    public View getEditLayout(Unit item, LayoutInflater layoutInflater) {
        View result = getEditingLayout(layoutInflater);
        EditText nameField = result.findViewById(R.id.form_unit_name);
        nameField.setText(item.getName());
        EditText abbreviationField = result.findViewById(R.id.form_unit_abbreviation);
        abbreviationField.setText(item.getAbbreviation());
        return result;
    }

    @Override
    public void treatStatusCode(Unit item, StatusCode statusCode) {
        maybeShowEditError(statusCode);
    }
}
