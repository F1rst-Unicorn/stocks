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

package de.njsm.stocks.client.fragment.foodadd;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.FoodAddForm;
import de.njsm.stocks.client.business.entities.LocationForSelection;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.fragment.view.FoodForm;
import de.njsm.stocks.client.navigation.Navigator;
import de.njsm.stocks.client.presenter.FoodAddViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;

public class FoodAddFragment extends BottomToolbarFragment {

    private FoodAddViewModel foodAddViewModel;

    private Navigator navigator;

    private FoodForm form;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        View result = insertContent(inflater, root, R.layout.fragment_food_form);
        form = new FoodForm(result, this::getString);
        form.setToBuy(true);

        foodAddViewModel.getUnits().observe(getViewLifecycleOwner(), form::showUnits);
        foodAddViewModel.getLocations().observe(getViewLifecycleOwner(), form::showLocations);

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
            form.showErrors();
            return true;
        }

        form.getStoreUnit().ifPresent(storeUnit -> {
            FoodAddForm data = FoodAddForm.create(
                    form.getName(),
                    form.getToBuy(),
                    form.getExpirationOffset(),
                    form.getLocation().map(LocationForSelection::id).orElse(null),
                    storeUnit.id(),
                    form.getDescription()
            );
            foodAddViewModel.add(data);
            navigator.back();
        });
        return true;
    }

    @Inject
    @Override
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        foodAddViewModel = viewModelProvider.get(FoodAddViewModel.class);
    }

    @Inject
    void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}
