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

import android.os.Bundle;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.google.android.material.textfield.TextInputLayout;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.entities.Location;
import de.njsm.stocks.android.db.views.ScaledUnitView;
import de.njsm.stocks.android.frontend.InjectedFragment;
import de.njsm.stocks.android.frontend.emptyfood.FoodViewModel;
import de.njsm.stocks.android.frontend.locations.LocationViewModel;
import de.njsm.stocks.android.frontend.units.ScaledUnitViewModel;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.util.livedata.JoiningLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.njsm.stocks.android.util.Utility.find;

public class FoodEditFragment extends InjectedFragment {

    FoodViewModel foodViewModel;

    LocationViewModel locationViewModel;

    ScaledUnitViewModel scaledUnitViewModel;

    TextInputLayout descriptionField;

    TextInputLayout nameField;

    TextInputLayout expirationOffsetField;

    Spinner locationSpinner;

    Spinner unitSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_food_edit, container, false);

        descriptionField = result.findViewById(R.id.fragment_food_edit_description);
        nameField = result.findViewById(R.id.fragment_food_edit_name);
        expirationOffsetField = result.findViewById(R.id.fragment_food_edit_expiration_offset);
        locationSpinner = result.findViewById(R.id.fragment_food_edit_location);
        unitSpinner = result.findViewById(R.id.fragment_food_edit_unit);

        foodViewModel = new ViewModelProvider(this, viewModelFactory).get(FoodViewModel.class);
        locationViewModel = new ViewModelProvider(this, viewModelFactory).get(LocationViewModel.class);
        scaledUnitViewModel = new ViewModelProvider(this, viewModelFactory).get(ScaledUnitViewModel.class);

        setHasOptionsMenu(true);
        return result;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialiseForm(view);
    }

    void initialiseForm(View result) {
        assert getArguments() != null;
        FoodEditFragmentArgs input = FoodEditFragmentArgs.fromBundle(getArguments());

        foodViewModel.initFood(input.getFoodId());
        foodViewModel.getFood().observe(getViewLifecycleOwner(), f -> {
            foodViewModel.getFood().removeObservers(getViewLifecycleOwner());
            descriptionField.getEditText().setText(f.description);
            nameField.getEditText().setText(f.name);
            expirationOffsetField.getEditText().setText(String.valueOf(f.expirationOffset));
        });

        fillLocationSpinner();
        fillStoreUnitSpinner();
        hideDiffLabels();

        initialiseCurrentSpinnerValues();
    }

    void initialiseCurrentSpinnerValues() {
        JoiningLiveData<Food, List<Location>> currentLocationData = new JoiningLiveData<>(foodViewModel.getFood(), locationViewModel.getLocations());
        currentLocationData.observe(getViewLifecycleOwner(), p -> {
            currentLocationData.removeObservers(getViewLifecycleOwner());
            find(p.first.location, p.second).ifPresent(v -> locationSpinner.setSelection(v + 1));
        });

        JoiningLiveData<Food, List<ScaledUnitView>> currentUnitData = new JoiningLiveData<>(foodViewModel.getFood(), scaledUnitViewModel.getUnits());
        currentUnitData.observe(getViewLifecycleOwner(), p -> {
            currentUnitData.removeObservers(getViewLifecycleOwner());
            find(p.first.storeUnit, p.second).ifPresent(unitSpinner::setSelection);
        });
    }

    void fillStoreUnitSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(),
                R.layout.item_unit_spinner, R.id.item_unit_spinner_name,
                new ArrayList<>());
        unitSpinner.setAdapter(adapter);
        scaledUnitViewModel.getUnits().observe(getViewLifecycleOwner(), l -> {
            List<String> data = l.stream().map(ScaledUnitView::getPrettyName).collect(Collectors.toList());
            adapter.clear();
            adapter.addAll(data);
            adapter.notifyDataSetChanged();
        });
    }

    void fillLocationSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(),
                R.layout.item_location, R.id.item_location_name,
                new ArrayList<>());
        locationSpinner.setAdapter(adapter);
        locationViewModel.getLocations().observe(getViewLifecycleOwner(), l -> {
            List<String> data = l.stream().map(i -> i.name).collect(Collectors.toList());
            adapter.clear();
            adapter.add("---");
            adapter.addAll(data);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_food_edit_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startEditing();
        return true;
    }

    void startEditing() {
        foodViewModel.getFood().removeObservers(getViewLifecycleOwner());
        foodViewModel.getFood().observe(getViewLifecycleOwner(), f -> {
            foodViewModel.getFood().removeObservers(getViewLifecycleOwner());
            edit(f);
        });
    }

    private void edit(Food food) {
        Food editedFood = food.copy();
        editedFood.description = getDescriptionText();
        editedFood.name = getNameText();
        editedFood.expirationOffset = getExpirationOffset();
        editedFood.storeUnit = getStoreUnit(editedFood);
        editedFood.location = getLocation(editedFood);

        if (food.equals(editedFood)) {
            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                    .navigateUp();
            return;
        }

        LiveData<StatusCode> result = foodViewModel.edit(editedFood);
        result.observe(getViewLifecycleOwner(), code -> {
            result.removeObservers(getViewLifecycleOwner());

            if (code == StatusCode.SUCCESS) {
                Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                        .navigateUp();

            } else if (code == StatusCode.INVALID_DATA_VERSION) {
                resolveConflict(editedFood);
            } else {
                showErrorMessage(requireActivity(), code.getEditErrorMessage());
            }
        });
    }

    void resolveConflict(Food food) {
        FoodEditFragmentDirections.ActionNavFragmentFoodEditToNavFragmentFoodConflict args =
            FoodEditFragmentDirections.actionNavFragmentFoodEditToNavFragmentFoodConflict(FoodInConflict.from(food));
        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                .navigate(args);
    }

    private int getLocation(Food editedFood) {
        List<Location> locations = locationViewModel.getLocations().getValue();
        if (locations != null) {
            if (locationSpinner.getSelectedItemPosition() == 0) {
                return 0;
            } else {
                return locations.get(locationSpinner.getSelectedItemPosition() - 1).id;
            }
        }
        return editedFood.location;
    }

    private int getStoreUnit(Food editedFood) {
        List<ScaledUnitView> units = scaledUnitViewModel.getUnits().getValue();
        if (units != null) {
            return units.get(unitSpinner.getSelectedItemPosition()).id;
        }
        return editedFood.storeUnit;
    }

    private int getExpirationOffset() {
        return Integer.parseInt(expirationOffsetField.getEditText().getText().toString().trim());
    }

    private String getDescriptionText() {
        return descriptionField.getEditText().getText().toString().trim();
    }

    private String getNameText() {
        return nameField.getEditText().getText().toString().trim();
    }

    void hideDiffLabels() {
        setNameVisibility(View.GONE);
        setExpirationOffsetVisibility(View.GONE);
        setLocationVisibility(View.GONE);
        setUnitVisibility(View.GONE);
    }

    void setNameVisibility(int visibility) {
        getView().findViewById(R.id.fragment_food_edit_name_original_label).setVisibility(visibility);
        getView().findViewById(R.id.fragment_food_edit_name_local_label).setVisibility(visibility);
        getView().findViewById(R.id.fragment_food_edit_name_remote_label).setVisibility(visibility);
        getView().findViewById(R.id.fragment_food_edit_name_original).setVisibility(visibility);
        getView().findViewById(R.id.fragment_food_edit_name_local).setVisibility(visibility);
        getView().findViewById(R.id.fragment_food_edit_name_remote).setVisibility(visibility);
    }

    void setExpirationOffsetVisibility(int visibility) {
        getView().findViewById(R.id.fragment_food_edit_expiration_offset_original_label).setVisibility(visibility);
        getView().findViewById(R.id.fragment_food_edit_expiration_offset_local_label).setVisibility(visibility);
        getView().findViewById(R.id.fragment_food_edit_expiration_offset_remote_label).setVisibility(visibility);
        getView().findViewById(R.id.fragment_food_edit_expiration_offset_original).setVisibility(visibility);
        getView().findViewById(R.id.fragment_food_edit_expiration_offset_local).setVisibility(visibility);
        getView().findViewById(R.id.fragment_food_edit_expiration_offset_remote).setVisibility(visibility);
    }

    void setLocationVisibility(int visibility) {
        getView().findViewById(R.id.fragment_food_edit_location_original_label).setVisibility(visibility);
        getView().findViewById(R.id.fragment_food_edit_location_local_label).setVisibility(visibility);
        getView().findViewById(R.id.fragment_food_edit_location_remote_label).setVisibility(visibility);
        getView().findViewById(R.id.fragment_food_edit_location_original).setVisibility(visibility);
        getView().findViewById(R.id.fragment_food_edit_location_local).setVisibility(visibility);
        getView().findViewById(R.id.fragment_food_edit_location_remote).setVisibility(visibility);
    }

    void setUnitVisibility(int visibility) {
        getView().findViewById(R.id.fragment_food_edit_unit_original_label).setVisibility(visibility);
        getView().findViewById(R.id.fragment_food_edit_unit_local_label).setVisibility(visibility);
        getView().findViewById(R.id.fragment_food_edit_unit_remote_label).setVisibility(visibility);
        getView().findViewById(R.id.fragment_food_edit_unit_original).setVisibility(visibility);
        getView().findViewById(R.id.fragment_food_edit_unit_local).setVisibility(visibility);
        getView().findViewById(R.id.fragment_food_edit_unit_remote).setVisibility(visibility);
    }
}
