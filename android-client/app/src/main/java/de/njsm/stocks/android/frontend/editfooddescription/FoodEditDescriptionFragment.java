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

package de.njsm.stocks.android.frontend.editfooddescription;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.emptyfood.FoodViewModel;
import de.njsm.stocks.android.network.server.StatusCode;

public class FoodEditDescriptionFragment extends BaseFragment {

    private ViewModelProvider.Factory viewModelFactory;

    private FoodViewModel foodViewModel;

    private EditText editText;

    @Override
    public void onAttach(@NonNull Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_edit_food_description, container, false);

        assert getArguments() != null;
        FoodEditDescriptionFragmentArgs input = FoodEditDescriptionFragmentArgs.fromBundle(getArguments());

        editText = result.findViewById(R.id.fragment_edit_food_description_text);

        foodViewModel = ViewModelProviders.of(this, viewModelFactory).get(FoodViewModel.class);
        foodViewModel.initFood(input.getFoodId());
        foodViewModel.getFood().observe(getViewLifecycleOwner(), f -> {
            editText.setText(f.description);
            editText.setSelection(f.description.length());
            foodViewModel.getFood().removeObservers(getViewLifecycleOwner());
        });

        setHasOptionsMenu(true);
        return result;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_food_edit_description_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String text = editText.getText().toString().trim();

        foodViewModel.getFood().removeObservers(getViewLifecycleOwner());
        foodViewModel.getFood().observe(getViewLifecycleOwner(), f -> {
            foodViewModel.getFood().removeObservers(getViewLifecycleOwner());
            setDescription(text, f);
        });

        return true;
    }

    private void setDescription(String text, Food food) {
        if (text.equals(food.description)) {
            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                    .navigateUp();
            return;
        }

        LiveData<StatusCode> result = foodViewModel.setFoodDescription(food.id, food.version, text);
        result.observe(getViewLifecycleOwner(), code -> {
            result.removeObservers(getViewLifecycleOwner());

            if (code == StatusCode.SUCCESS) {
                Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                        .navigateUp();

            } else if (code == StatusCode.INVALID_DATA_VERSION) {
                foodViewModel.getFood().observe(getViewLifecycleOwner(), newFood -> {
                    if (newFood.version != food.version) {
                        foodViewModel.getFood().removeObservers(getViewLifecycleOwner());
                        if (!food.description.equals(newFood.description)) {
                            String newText = String.format("%s:\n%s\n\n%s:\n%s\n\n%s:\n%s",
                                    getString(R.string.hint_original),
                                    food.description,
                                    getString(R.string.hint_local),
                                    text,
                                    getString(R.string.hint_remote),
                                    newFood.description);
                            editText.setText(newText);
                            editText.setSelection(newText.length());
                            showErrorMessage(requireActivity(), R.string.dialog_conflicting_description);
                        } else {
                            setDescription(text, newFood);
                        }
                    }
                });

            } else {
                showErrorMessage(requireActivity(), code.getEditErrorMessage());
            }
        });
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }
}
