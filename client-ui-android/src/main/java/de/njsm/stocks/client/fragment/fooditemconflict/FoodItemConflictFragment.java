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

package de.njsm.stocks.client.fragment.fooditemconflict;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.Localiser;
import de.njsm.stocks.client.business.entities.FoodItem;
import de.njsm.stocks.client.business.entities.FoodItemToEdit;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.fragment.view.FoodItemForm;
import de.njsm.stocks.client.navigation.FoodItemConflictNavigator;
import de.njsm.stocks.client.presenter.FoodItemConflictViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;

public class FoodItemConflictFragment extends BottomToolbarFragment {

    private FoodItemConflictNavigator navigator;

    private FoodItemConflictViewModel viewModel;

    private FoodItemForm form;

    private Id<FoodItem> id;

    private Localiser localiser;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        this.form = new FoodItemForm(insertContent(inflater, root, R.layout.fragment_food_item_form));
        form.setToday(localiser.today());

        long errorId = navigator.getErrorId(requireArguments());

        viewModel.getFoodEditConflict(errorId).observe(getViewLifecycleOwner(), v -> {
            id = v;
            form.setPredictionDate(v.eatBy().suggestedValue());
            form.showLocations(v.locations());
            form.showUnits(v.units());

            if (v.hasNoConflict()) {
                submit();
                return;
            }

            if (v.eatBy().needsHandling()) {
                form.showEatByConflict(v.eatBy(), localiser);
            } else {
                form.hideEatBy();
            }
            if (v.location().needsHandling()) {
                form.showLocationConflict(v.location());
            } else {
                form.hideLocation();
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
        FoodItemToEdit editedFoodItem = FoodItemToEdit.create(
                id.id(),
                form.eatBy(),
                form.storedIn().id(),
                form.unit().id()
        );

        viewModel.edit(editedFoodItem);
        navigator.back();
    }

    @Inject
    @Override
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        viewModel = viewModelProvider.get(FoodItemConflictViewModel.class);
    }

    @Inject
    void setNavigator(FoodItemConflictNavigator navigator) {
        this.navigator = navigator;
    }

    @Inject
    void setLocaliser(Localiser localiser) {
        this.localiser = localiser;
    }
}
