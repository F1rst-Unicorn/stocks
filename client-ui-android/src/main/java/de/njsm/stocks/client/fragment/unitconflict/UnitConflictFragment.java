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

package de.njsm.stocks.client.fragment.unitconflict;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.Identifiable;
import de.njsm.stocks.client.business.entities.Unit;
import de.njsm.stocks.client.business.entities.UnitToEdit;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.fragment.view.UnitForm;
import de.njsm.stocks.client.navigation.UnitConflictNavigator;
import de.njsm.stocks.client.presenter.UnitConflictViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;

public class UnitConflictFragment extends BottomToolbarFragment {

    private UnitConflictViewModel unitConflictViewModel;

    private UnitConflictNavigator unitConflictNavigator;

    private UnitForm form;

    private Identifiable<Unit> id;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        this.form = new UnitForm(insertContent(inflater, root, R.layout.fragment_unit_form), this::getString);

        long errorId = unitConflictNavigator.getErrorId(requireArguments());
        unitConflictViewModel.getUnitEditConflict(errorId).observe(getViewLifecycleOwner(), v -> {
            id = v;
            form.setName(v.name().suggestedValue());
            form.setAbbreviation(v.abbreviation().suggestedValue());

            if (v.hasNoConflict()) {
                submit();
                return;
            }

            if (v.name().needsHandling()) {
                form.showNameConflict(v.name());
            } else {
                form.hideName();
            }

            if (v.abbreviation().needsHandling()) {
                form.showAbbreviationConflict(v.abbreviation());
            } else {
                form.hideAbbreviation();
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

        UnitToEdit data = UnitToEdit.builder()
                .id(id.id())
                .name(form.getName())
                .abbreviation(form.getAbbreviation())
                .build();
        unitConflictViewModel.edit(data);
        unitConflictNavigator.back();
    }

    @Inject
    @Override
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        unitConflictViewModel = viewModelProvider.get(UnitConflictViewModel.class);
    }

    @Inject
    void setUnitConflictNavigator(UnitConflictNavigator unitConflictNavigator) {
        this.unitConflictNavigator = unitConflictNavigator;
    }
}
