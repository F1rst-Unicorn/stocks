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

package de.njsm.stocks.client.fragment.foodconflict;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.FoodToEdit;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.LocationForSelection;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.fragment.view.FoodForm;
import de.njsm.stocks.client.navigation.FoodConflictNavigator;
import de.njsm.stocks.client.presenter.FoodConflictViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;

public class FoodConflictFragment extends BottomToolbarFragment {

    private FoodConflictNavigator navigator;

    private FoodConflictViewModel viewModel;

    private FoodForm form;

    private Id<Food> id;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        this.form = new FoodForm(insertContent(inflater, root, R.layout.fragment_food_form), this::getString);
        form.hideToBuy();

        long errorId = navigator.getErrorId(requireArguments());

        viewModel.getFoodEditConflict(errorId).observe(getViewLifecycleOwner(), v -> {
            id = v;
            form.setName(v.name());
            form.setExpirationOffset(v.expirationOffset());
            form.showLocations(v.availableLocations(), v.currentLocationListPosition());
            form.showUnits(v.availableStoreUnits(), v.currentScaledUnitListPosition());
            form.setDescription(String.format(v.description().suggestedValue(),
                    getString(R.string.hint_original),
                    getString(R.string.hint_remote),
                    getString(R.string.hint_local)
            ));

            if (v.hasNoConflict()) {
                submit();
                return;
            }

            if (v.name().needsHandling()) {
                form.showNameConflict(v.name());
            } else {
                form.hideName();
            }
            if (v.expirationOffset().needsHandling()) {
                form.showExpirationOffsetConflict(v.expirationOffset());
            } else {
                form.hideExpirationOffset();
            }
            if (v.location().needsHandling()) {
                form.showLocationConflict(v.location());
            } else {
                form.hideLocation();
            }
            if (v.storeUnit().needsHandling()) {
                form.showStoreUnitConflict(v.storeUnit());
            } else {
                form.hideStoreUnit();
            }
            if (!v.description().needsHandling()) {
                form.hideDescription();
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
            form.showErrors();
            return;
        }

        form.getStoreUnit().ifPresent(unit -> {
            FoodToEdit editedFood = FoodToEdit.create(
                    id.id(),
                    form.getName(),
                    form.getExpirationOffset(),
                    form.getLocation().map(LocationForSelection::id).orElse(null),
                    unit.id(),
                    form.getDescription()
            );

            viewModel.edit(editedFood);
            navigator.back();
        });
    }

    @Inject
    @Override
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        viewModel = viewModelProvider.get(FoodConflictViewModel.class);
    }

    @Inject
    void setNavigator(FoodConflictNavigator navigator) {
        this.navigator = navigator;
    }
}
