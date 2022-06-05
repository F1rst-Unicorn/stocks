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
 */

package de.njsm.stocks.client.fragment.scaledunitadd;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.ScaledUnitAddForm;
import de.njsm.stocks.client.business.entities.UnitForSelection;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.fragment.view.ScaledUnitForm;
import de.njsm.stocks.client.navigation.Navigator;
import de.njsm.stocks.client.presenter.ScaledUnitAddViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;
import java.util.Optional;

public class ScaledUnitAddFragment extends BottomToolbarFragment {

    private ScaledUnitAddViewModel scaledUnitAddViewModel;

    private Navigator navigator;

    private ScaledUnitForm form;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        View ownContent = insertContent(inflater, root, R.layout.fragment_scaled_unit_form);
        form = new ScaledUnitForm(ownContent, this::getString);

        scaledUnitAddViewModel.getUnits().observe(getViewLifecycleOwner(), form::showUnits);

        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.check, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (!form.maySubmit()) {
            form.setError(R.string.error_may_not_be_empty);
            return true;
        }

        Optional<UnitForSelection> unitFromForm = form.getUnit();

        unitFromForm.ifPresent(unit -> {
            ScaledUnitAddForm data = ScaledUnitAddForm.create(
                    form.getScale(),
                    unit.id()
            );
            scaledUnitAddViewModel.addScaledUnit(data);
            navigator.back();
        });
        return true;
    }

    @Inject
    @Override
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        scaledUnitAddViewModel = viewModelProvider.get(ScaledUnitAddViewModel.class);
    }

    @Inject
    void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}
