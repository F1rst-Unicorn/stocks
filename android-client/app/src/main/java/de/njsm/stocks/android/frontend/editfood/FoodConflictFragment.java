/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2021  The stocks developers
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

package de.njsm.stocks.android.frontend.editfood;

import android.view.View;
import android.widget.TextView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.navigation.Navigation;
import de.njsm.stocks.R;
import de.njsm.stocks.android.business.data.conflict.FoodComparison;
import de.njsm.stocks.android.business.data.conflict.FoodInConflict;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.util.livedata.ThreeWayJoiningLiveData;

public class FoodConflictFragment extends FoodEditFragment {

    private final MutableLiveData<Integer> locationPreselection;

    private final MutableLiveData<Integer> storeUnitPreselection;

    public FoodConflictFragment() {
        locationPreselection = new MutableLiveData<>();
        storeUnitPreselection = new MutableLiveData<>();
    }

    @Override
    void initialiseForm(View view) {
        assert getArguments() != null;
        FoodConflictFragmentArgs input = FoodConflictFragmentArgs.fromBundle(getArguments());

        foodViewModel.initFood(input.getFood().getId());

        setOriginalDiffLabels(view, input);
        setRemoteDiffLabels(view, input);
        setLocalDiffLabels(view, input);

        ThreeWayJoiningLiveData<FoodInConflict, FoodInConflict, FoodInConflict> foodVersions =
                new ThreeWayJoiningLiveData<>(
                        Transformations.map(foodViewModel.getFoodNowAsKnownBy(input.getFood().getId(), input.getFood().getTransactionTimeStart()), FoodInConflict::from),
                        Transformations.map(foodViewModel.getFood(), FoodInConflict::from),
                        new MutableLiveData<>(input.getFood())
                );

        fillLocationSpinner();
        fillStoreUnitSpinner();
        hideConflictLabels();

        foodVersions.observe(getViewLifecycleOwner(), versions -> {
            FoodComparison comparison = new FoodComparison(versions.t1, versions.t2, versions.t3);
            boolean anyManualResolutionRequired = comparison.compareName(this::setName);
            anyManualResolutionRequired |= comparison.compareExpirationOffset(this::setExpirationOffset);
            anyManualResolutionRequired |= comparison.compareLocation(this::setLocation);
            anyManualResolutionRequired |= comparison.compareStoreUnit(this::setStoreUnit);
            anyManualResolutionRequired |= comparison.compareDescription(this::getString, this::setDescription);

            if (!anyManualResolutionRequired) {
                startFormSubmission();
            }
        });
    }

    private void setRemoteDiffLabels(View view, FoodConflictFragmentArgs input) {
        foodViewModel.getFood().observe(getViewLifecycleOwner(), remote -> {
            if (remote.version != input.getFood().getVersion()) {
                ((TextView) view.findViewById(R.id.fragment_food_edit_name_remote)).setText(remote.name);
                ((TextView) view.findViewById(R.id.fragment_food_edit_expiration_offset_remote)).setText(String.valueOf(remote.expirationOffset));
                locationViewModel.getLocation(remote.location).observe(getViewLifecycleOwner(), location ->
                        ((TextView) view.findViewById(R.id.fragment_food_edit_location_remote)).setText(location.name));
                scaledUnitViewModel.getUnit(remote.storeUnit).observe(getViewLifecycleOwner(), unit ->
                        ((TextView) view.findViewById(R.id.fragment_food_edit_unit_remote)).setText(unit.getPrettyName()));
            }
        });
    }

    private void setOriginalDiffLabels(View view, FoodConflictFragmentArgs input) {
        foodViewModel.getFoodNowAsKnownBy(input.getFood().getId(), input.getFood().getTransactionTimeStart())
                .observe(getViewLifecycleOwner(), original -> {
                    ((TextView) view.findViewById(R.id.fragment_food_edit_name_original)).setText(original.name);
                    ((TextView) view.findViewById(R.id.fragment_food_edit_expiration_offset_original)).setText(String.valueOf(original.expirationOffset));
                    locationViewModel.getLocation(original.location).observe(getViewLifecycleOwner(), location ->
                            ((TextView) view.findViewById(R.id.fragment_food_edit_location_original)).setText(location.name));
                    scaledUnitViewModel.getUnit(original.storeUnit).observe(getViewLifecycleOwner(), unit ->
                            ((TextView) view.findViewById(R.id.fragment_food_edit_unit_original)).setText(unit.getPrettyName()));
                });
    }

    private void setLocalDiffLabels(View view, FoodConflictFragmentArgs input) {
        ((TextView) view.findViewById(R.id.fragment_food_edit_name_local)).setText(input.getFood().getName());
        ((TextView) view.findViewById(R.id.fragment_food_edit_expiration_offset_local)).setText(String.valueOf(input.getFood().getExpirationOffset()));
        locationViewModel.getLocation(input.getFood().getLocation()).observe(getViewLifecycleOwner(), location ->
                ((TextView) view.findViewById(R.id.fragment_food_edit_location_local)).setText(location.name));
        scaledUnitViewModel.getUnit(input.getFood().getStoreUnit()).observe(getViewLifecycleOwner(), unit ->
                ((TextView) view.findViewById(R.id.fragment_food_edit_unit_local)).setText(unit.getPrettyName()));
    }

    private void setDescription(String mergedDescription, boolean visible) {
        descriptionField.getEditText().setText(mergedDescription);

        if (!visible) {
            descriptionField.setVisibility(View.GONE);
        }
    }

    private void setLocation(int locationId, boolean visible) {
        locationPreselection.setValue(locationId);
        if (visible) {
            setLocationVisibility(View.VISIBLE);
        } else {
            locationSpinner.setVisibility(View.GONE);
        }
    }

    private void setStoreUnit(int storeUnitId, boolean visible) {
        storeUnitPreselection.setValue(storeUnitId);
        if (visible) {
            setUnitVisibility(View.VISIBLE);
        } else {
            unitSpinner.setVisibility(View.GONE);
        }
    }

    private void setExpirationOffset(Integer integer, boolean visible) {
        expirationOffsetField.getEditText().setText(String.valueOf(integer));
        if (visible) {
            setExpirationOffsetVisibility(View.VISIBLE);
        } else {
            expirationOffsetField.setVisibility(View.GONE);
        }
    }

    private void setName(String name, boolean visible) {
        nameField.getEditText().setText(name);
        if (visible) {
            setNameVisibility(View.VISIBLE);
        } else {
            nameField.setVisibility(View.GONE);
        }
    }

    @Override
    LiveData<Integer> getLocationPreselection() {
        return locationPreselection;
    }

    @Override
    LiveData<Integer> getStoreUnitPreselection() {
        return storeUnitPreselection;
    }

    @Override
    void resolveConflict(Food food) {
        FoodConflictFragmentDirections.ActionNavFragmentFoodConflictToNavFragmentFoodConflict args =
                FoodConflictFragmentDirections.actionNavFragmentFoodConflictToNavFragmentFoodConflict(FoodInConflict.from(food));
        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                .navigate(args);
    }
}
