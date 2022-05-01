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

package de.njsm.stocks.client.fragment.unitadd;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.UnitAddForm;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.navigation.Navigator;
import de.njsm.stocks.client.presenter.UnitAddViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;

public class UnitAddFragment extends BottomToolbarFragment {

    private UnitAddViewModel unitAddViewModel;

    private Navigator navigator;

    private UnitForm form;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        View result = insertContent(inflater, root, R.layout.fragment_unit_form);
        form = new UnitForm(result, this::getString);

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

        UnitAddForm data = UnitAddForm.create(
                form.getName(),
                form.getAbbreviation()
        );
        unitAddViewModel.addUnit(data);
        navigator.back();
        return true;
    }

    @Override
    @Inject
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        unitAddViewModel = viewModelProvider.get(UnitAddViewModel.class);
    }

    @Inject
    void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}
