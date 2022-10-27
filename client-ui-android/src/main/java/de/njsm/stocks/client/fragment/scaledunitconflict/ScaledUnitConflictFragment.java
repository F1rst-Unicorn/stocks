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

package de.njsm.stocks.client.fragment.scaledunitconflict;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.ScaledUnit;
import de.njsm.stocks.client.business.entities.ScaledUnitToEdit;
import de.njsm.stocks.client.business.entities.UnitForSelection;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.fragment.view.ScaledUnitForm;
import de.njsm.stocks.client.navigation.ScaledUnitConflictNavigator;
import de.njsm.stocks.client.presenter.ScaledUnitConflictViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;
import java.util.Optional;

public class ScaledUnitConflictFragment extends BottomToolbarFragment {

    private ScaledUnitConflictNavigator navigator;

    private ScaledUnitConflictViewModel viewModel;

    private ScaledUnitForm form;

    private Id<ScaledUnit> id;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        this.form = new ScaledUnitForm(insertContent(inflater, root, R.layout.fragment_scaled_unit_form), this::getString);

        long errorId = navigator.getErrorId(requireArguments());

        viewModel.getScaledUnitEditConflict(errorId).observe(getViewLifecycleOwner(), v -> {
            id = v;
            form.showScale(v.scale());
            form.showUnits(v.availableUnits());
            form.preSelectUnitPosition(v.currentUnitListPosition());

            if (v.hasNoConflict()) {
                submit();
                return;
            }

            if (v.scale().needsHandling()) {
                form.showScaleConflict(v.scale());
            } else {
                form.hideScale();
            }

            if (v.unit().needsHandling()) {
                form.showUnitConflict(v.unit());
            } else {
                form.hideUnit();
            }
        });

        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.check, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        submit();
        return true;
    }

    private void submit() {
        if (!form.maySubmit()) {
            form.setError(R.string.error_may_not_be_empty);
            return;
        }

        Optional<UnitForSelection> unitFromForm = form.getUnit();

        unitFromForm.ifPresent(unit -> {
            ScaledUnitToEdit editedScaledUnit = ScaledUnitToEdit.create(
                    this.id.id(),
                    form.getScale(),
                    unit.id()
            );

            viewModel.edit(editedScaledUnit);
            navigator.back();
        });
    }

    @Inject
    @Override
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        viewModel = viewModelProvider.get(ScaledUnitConflictViewModel.class);
    }

    @Inject
    void setNavigator(ScaledUnitConflictNavigator navigator) {
        this.navigator = navigator;
    }
}
