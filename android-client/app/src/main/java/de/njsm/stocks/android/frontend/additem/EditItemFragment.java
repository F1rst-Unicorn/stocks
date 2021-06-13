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

package de.njsm.stocks.android.frontend.additem;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.navigation.Navigation;
import de.njsm.stocks.R;
import de.njsm.stocks.android.business.data.conflict.FoodItemInConflict;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.views.FoodItemView;
import de.njsm.stocks.android.network.server.StatusCode;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;


public class EditItemFragment extends AddItemFragment {

    @Override
    void initialiseForm(View view) {
        assert getArguments() != null;
        EditItemFragmentArgs input = EditItemFragmentArgs.fromBundle(getArguments());

        viewModel.init(input.getFoodItemId());

        setDateField(LocalDate.now());
        fillLocationSpinner();
        fillUnitSpinner();
        hideConflictLabels();

        viewModel.getItem().observe(this, i -> {
            LocalDate date = LocalDate.from(i.getEatByDate().atZone(ZoneId.systemDefault()));
            setDateField(date);
        });

        viewModel.getItem().observe(this, i -> {
            LiveData<Food> food = foodViewModel.getFood(i.getOfType());
            food.observe(this, f -> {
                food.removeObservers(this);
                String title = getString(R.string.title_edit_item, f.name);
                requireActivity().setTitle(title);
            });
        });
    }

    @Override
    LiveData<Integer> getLocationPreselection() {
        return Transformations.map(viewModel.getItem(), FoodItemView::getStoredIn);
    }

    @Override
    LiveData<Integer> getUnitPreselection() {
        return Transformations.map(viewModel.getItem(), FoodItemView::getUnit);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_edit_item_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startFormSubmission();
        return true;
    }

    @Override
    void startFormSubmission() {
        viewModel.getItem().observe(getViewLifecycleOwner(), f -> {
            viewModel.getItem().removeObservers(getViewLifecycleOwner());
            submitForm(f);
        });
    }

    private void submitForm(FoodItemView item) {
        FoodItemView editedItem = item.copy();
        editedItem.setEatByDate(Instant.from(readDateFromPicker().atStartOfDay().atZone(ZoneId.of("UTC"))));
        editedItem.setStoredIn(readLocation());
        editedItem.setUnit(readScaledUnit());

        if (editedItem.equals(item)) {
            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                    .navigateUp();
            return;
        }

        LiveData<StatusCode> result = viewModel.edit(editedItem);
        result.observe(getViewLifecycleOwner(), code -> {
            result.removeObservers(getViewLifecycleOwner());

            if (code == StatusCode.SUCCESS) {
                Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                        .navigateUp();

            } else if (code == StatusCode.INVALID_DATA_VERSION) {
                resolveConflict(editedItem);
            } else {
                showErrorMessage(requireActivity(), code.getEditErrorMessage());
            }
        });
    }

    void resolveConflict(FoodItemView editedItem) {
        EditItemFragmentDirections.ActionNavFragmentEditFoodItemToNavFragmentEditFoodItemConflict args =
                EditItemFragmentDirections.actionNavFragmentEditFoodItemToNavFragmentEditFoodItemConflict(FoodItemInConflict.from(editedItem));
        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                .navigate(args);
    }
}
