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

package de.njsm.stocks.client.fragment.foodedit;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.fragment.view.FoodForm;
import de.njsm.stocks.client.navigation.FoodEditNavigator;
import de.njsm.stocks.client.presenter.FoodEditViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;

public class FoodEditFragment extends BottomToolbarFragment {

    private FoodEditViewModel foodEditViewModel;

    private FoodEditNavigator navigator;

    private FoodForm form;

    private Identifiable<Food> id;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        View result = insertContent(inflater, root, R.layout.fragment_food_form);
        form = new FoodForm(result, this::getString);

        id = navigator.getId(requireArguments());
        foodEditViewModel.getFormData(id).observe(getViewLifecycleOwner(), this::fillForm);
        form.hideToBuy();

        setHasOptionsMenu(true);
        return root;
    }

    private void fillForm(FoodEditingFormData foodEditingFormData) {
        form.setName(foodEditingFormData.name());
        form.setExpirationOffset(foodEditingFormData.expirationOffset());
        form.showLocations(foodEditingFormData.locations(), foodEditingFormData.currentLocationListPosition());
        form.showUnits(foodEditingFormData.storeUnits(), foodEditingFormData.currentStoreUnitListPosition());
        form.setDescription(foodEditingFormData.description());
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
            FoodToEdit data = FoodToEdit.create(
                    id.id(),
                    form.getName(),
                    form.getExpirationOffset(),
                    form.getLocation().map(LocationForSelection::id).orElse(null),
                    storeUnit.id(),
                    form.getDescription()
            );

            foodEditViewModel.edit(data);
            navigator.back();
        });
        return true;
    }

    @Inject
    @Override
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        foodEditViewModel = viewModelProvider.get(FoodEditViewModel.class);
    }

    @Inject
    void setNavigator(FoodEditNavigator navigator) {
        this.navigator = navigator;
    }
}
