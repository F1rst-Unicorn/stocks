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

package de.njsm.stocks.android.frontend.additem;

import android.view.View;
import android.widget.TextView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.navigation.Navigation;
import de.njsm.stocks.R;
import de.njsm.stocks.android.business.data.conflict.FoodItemComparison;
import de.njsm.stocks.android.business.data.conflict.FoodItemInConflict;
import de.njsm.stocks.android.db.views.FoodItemView;
import de.njsm.stocks.android.db.views.ScaledUnitView;
import de.njsm.stocks.android.util.Config;
import de.njsm.stocks.android.util.livedata.ThreeWayJoiningLiveData;
import org.threeten.bp.Instant;

public class FoodItemConflictFragment extends EditItemFragment {

    private final MutableLiveData<Integer> locationPreselection;

    private final MutableLiveData<Integer> unitPreselection;

    public FoodItemConflictFragment() {
        locationPreselection = new MutableLiveData<>();
        unitPreselection = new MutableLiveData<>();
    }

    @Override
    void initialiseForm(View view) {
        assert getArguments() != null;
        FoodItemConflictFragmentArgs input = FoodItemConflictFragmentArgs.fromBundle(getArguments());

        viewModel.init(input.getFoodItem().getId());

        setOriginalDiffLabels(view, input);
        setRemoteDiffLabels(view, input);
        setLocalDiffLabels(view, input);

        ThreeWayJoiningLiveData<FoodItemInConflict, FoodItemInConflict, FoodItemInConflict> foodVersions =
                new ThreeWayJoiningLiveData<>(
                        Transformations.map(viewModel.getNowAsKnownBy(input.getFoodItem().getId(), input.getFoodItem().getTransactionTimeStart()), FoodItemInConflict::from),
                        Transformations.map(viewModel.getItem(), FoodItemInConflict::from),
                        new MutableLiveData<>(input.getFoodItem())
                );

        fillLocationSpinner();
        fillUnitSpinner();
        hideConflictLabels();

        foodVersions.observe(getViewLifecycleOwner(), versions -> {
            FoodItemComparison comparison = new FoodItemComparison(versions.t1, versions.t2, versions.t3);
            boolean anyManualResolutionRequired = comparison.compareEatByDate(this::setEatByDate);
            anyManualResolutionRequired |= comparison.compareLocation(this::setLocation);
            anyManualResolutionRequired |= comparison.compareUnit(this::setUnit);

            if (!anyManualResolutionRequired) {
                startFormSubmission();
            }
        });
    }

    private void setRemoteDiffLabels(View view, FoodItemConflictFragmentArgs input) {
        viewModel.getItem().observe(getViewLifecycleOwner(), remote -> {
            if (remote.version != input.getFoodItem().getVersion()) {
                ((TextView) view.findViewById(R.id.fragment_add_food_item_date_conflict)
                        .findViewById(R.id.template_conflict_labels_remote)).setText(Config.PRETTY_DATE_FORMAT.format(remote.getEatByDate()));
                ((TextView) view.findViewById(R.id.fragment_add_food_item_location_conflict)
                        .findViewById(R.id.template_conflict_labels_remote)).setText(remote.getLocation().name);
                ((TextView) view.findViewById(R.id.fragment_add_food_item_unit_conflict)
                        .findViewById(R.id.template_conflict_labels_remote)).setText(ScaledUnitView.getPrettyName(remote.getScaledUnit(), remote.getUnitEntity()));
            }
        });
    }

    private void setOriginalDiffLabels(View view, FoodItemConflictFragmentArgs input) {
        viewModel.getNowAsKnownBy(input.getFoodItem().getId(), input.getFoodItem().getTransactionTimeStart())
                .observe(getViewLifecycleOwner(), original -> {
                    ((TextView) view.findViewById(R.id.fragment_add_food_item_date_conflict).findViewById(R.id.template_conflict_labels_original))
                            .setText(Config.PRETTY_DATE_FORMAT.format(original.getEatByDate()));
                    ((TextView) view.findViewById(R.id.fragment_add_food_item_location_conflict).findViewById(R.id.template_conflict_labels_original))
                            .setText(original.getLocation().name);
                    ((TextView) view.findViewById(R.id.fragment_add_food_item_unit_conflict).findViewById(R.id.template_conflict_labels_original))
                            .setText(ScaledUnitView.getPrettyName(original.getScaledUnit(), original.getUnitEntity()));
                });
    }

    private void setLocalDiffLabels(View view, FoodItemConflictFragmentArgs input) {
        ((TextView) view.findViewById(R.id.fragment_add_food_item_date_conflict)
                .findViewById(R.id.template_conflict_labels_local)).setText(Config.PRETTY_DATE_FORMAT.format(input.getFoodItem().getEatByDate()));
        locationViewModel.getLocation(input.getFoodItem().getLocation()).observe(getViewLifecycleOwner(), location ->
                ((TextView) view.findViewById(R.id.fragment_add_food_item_location_conflict).findViewById(R.id.template_conflict_labels_local))
                        .setText(location.name));
        scaledUnitViewModel.getUnit(input.getFoodItem().getUnit()).observe(getViewLifecycleOwner(), unit ->
                ((TextView) view.findViewById(R.id.fragment_add_food_item_unit_conflict).findViewById(R.id.template_conflict_labels_local))
                        .setText(unit.getPrettyName()));
    }

    private void setEatByDate(Instant date, boolean visible) {
        setDateField(date);
        if (visible) {
            setDateVisibility(View.VISIBLE);
        } else {
            dateField.setVisibility(View.GONE);
        }
    }

    private void setLocation(int locationId, boolean visible) {
        locationPreselection.setValue(locationId);
        if (visible) {
            setLocationVisibility(View.VISIBLE);
        } else {
            locationField.setVisibility(View.GONE);
        }
    }

    private void setUnit(int storeUnitId, boolean visible) {
        unitPreselection.setValue(storeUnitId);
        if (visible) {
            setUnitVisibility(View.VISIBLE);
        } else {
            unitField.setVisibility(View.GONE);
        }
    }

    @Override
    LiveData<Integer> getLocationPreselection() {
        return locationPreselection;
    }

    @Override
    LiveData<Integer> getUnitPreselection() {
        return unitPreselection;
    }

    @Override
    void resolveConflict(FoodItemView food) {
        FoodItemConflictFragmentDirections.ActionNavFragmentEditFoodItemConflictToNavFragmentEditFoodItemConflict args =
                FoodItemConflictFragmentDirections.actionNavFragmentEditFoodItemConflictToNavFragmentEditFoodItemConflict(FoodItemInConflict.from(food));
        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                .navigate(args);
    }
}
