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

import android.os.Bundle;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.entities.Location;
import de.njsm.stocks.android.db.views.ScaledUnitView;
import de.njsm.stocks.android.frontend.InjectedFragment;
import de.njsm.stocks.android.frontend.emptyfood.FoodViewModel;
import de.njsm.stocks.android.frontend.fooditem.FoodItemViewModel;
import de.njsm.stocks.android.frontend.locations.LocationViewModel;
import de.njsm.stocks.android.frontend.units.ScaledUnitViewModel;
import de.njsm.stocks.android.frontend.util.SpinnerSynchroniser;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.util.Logger;
import de.njsm.stocks.android.util.livedata.Transformator;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.Period;
import org.threeten.bp.ZoneId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class AddItemFragment extends InjectedFragment {

    private static final Logger LOG = new Logger(AddItemFragment.class);

    DatePicker dateField;

    Spinner locationField;

    Spinner unitField;

    FoodItemViewModel viewModel;

    FoodViewModel foodViewModel;

    ScaledUnitViewModel scaledUnitViewModel;

    LocationViewModel locationViewModel;

    private AddItemFragmentArgs input;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_add_food_item, container, false);

        dateField = result.findViewById(R.id.fragment_add_food_item_date);
        locationField = result.findViewById(R.id.fragment_add_food_item_location);
        unitField = result.findViewById(R.id.fragment_add_food_item_unit);

        viewModel = new ViewModelProvider(this, viewModelFactory).get(FoodItemViewModel.class);
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

    void initialiseForm(View view) {
        assert getArguments() != null;
        input = AddItemFragmentArgs.fromBundle(getArguments());
        foodViewModel.initFood(input.getFoodId());

        fillLocationSpinner();
        fillUnitSpinner();
        hideConflictLabels();

        LiveData<Food> food = foodViewModel.getFood();
        food.observe(getViewLifecycleOwner(), f -> {
            food.removeObservers(getViewLifecycleOwner());
            String title = getString(R.string.title_add_item, f.name);
            requireActivity().setTitle(title);

            setDatePicker(f);
        });
    }

    private void setDatePicker(Food f) {
        LocalDate now = LocalDate.now();
        setDateField(now);
        if (f.expirationOffset != 0) {
            setDateField(now.plus(Period.ofDays(f.expirationOffset)));
        } else {
            initialiseDatePickerFromExistingFood(input.getFoodId());
        }
    }

    void fillUnitSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(),
                R.layout.item_scaled_unit_spinner, R.id.item_scaled_unit_spinner_name,
                new ArrayList<>());
        unitField.setAdapter(adapter);
        LiveData<List<ScaledUnitView>> liveData = scaledUnitViewModel.getUnits();
        liveData.observe(getViewLifecycleOwner(), l -> {
            List<String> data = l.stream().map(ScaledUnitView::getPrettyName).collect(Collectors.toList());
            adapter.clear();
            adapter.addAll(data);
            adapter.notifyDataSetChanged();
        });

        new SpinnerSynchroniser<>(getViewLifecycleOwner(),
                liveData,
                getUnitPreselection(),
                unitField::setSelection);
    }

    LiveData<Integer> getUnitPreselection() {
        return Transformations.map(foodViewModel.getFood(), Food::getStoreUnit);
    }


    void fillLocationSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(),
                R.layout.item_location, R.id.item_location_name,
                new ArrayList<>());
        locationField.setAdapter(adapter);
        LiveData<List<Location>> liveData = locationViewModel.getLocations();
        liveData.observe(getViewLifecycleOwner(), locations -> {
            List<String> data = locations
                    .stream()
                    .map(i -> i.name)
                    .collect(Collectors.toList());
            adapter.clear();
            adapter.addAll(data);
            adapter.notifyDataSetChanged();
        });

        new SpinnerSynchroniser<>(getViewLifecycleOwner(),
                liveData,
                getLocationPreselection(),
                locationField::setSelection);
    }

    LiveData<Integer> getLocationPreselection() {
        return Transformations.switchMap(foodViewModel.getFood(), food -> {
            LiveData<Location> defaultLocation;
            if (food != null && food.location != 0) {
                defaultLocation = locationViewModel.getLocation(food.location);
            } else {
                defaultLocation = locationViewModel.getLocationWithMostItemsOfType(input.getFoodId());
            }
            defaultLocation = Transformator.noNull(defaultLocation);
            return Transformations.map(defaultLocation, Location::getId);
        });
    }

    private void initialiseDatePickerFromExistingFood(int foodId) {
        LiveData<Instant> latestExpiration = viewModel.getLatestExpirationOf(foodId);
        latestExpiration.observe(getViewLifecycleOwner(), i -> {
            if (i != null) {
                LocalDate date = LocalDate.from(i.atZone(ZoneId.systemDefault()));
                setDateField(date);
                latestExpiration.removeObservers(getViewLifecycleOwner());
            }
        });
    }

    void setDateField(LocalDate date) {
        this.dateField.init(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth(), null);
    }

    void setDateField(Instant date) {
        setDateField(date.atZone(ZoneId.systemDefault()).toLocalDate());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_add_item_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fragment_add_item_options_done:
                startFormSubmission();
                Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                        .navigateUp();
                break;
            case R.id.fragment_add_item_options_add_more:
                startFormSubmission();
                break;
        }
        return true;
    }

    void startFormSubmission() {
        LocalDate finalDate = readDateFromPicker();
        int locId = readLocation();

        if (locId == 0) {
            LOG.d("no location selected");
            new AlertDialog.Builder(requireContext())
                    .setIcon(R.drawable.ic_error_black_24dp)
                    .setTitle(getResources().getString(R.string.title_error))
                    .setMessage(getResources().getString(R.string.error_no_location_present))
                    .setCancelable(false)
                    .setPositiveButton(R.string.dialog_ok, this::doNothing)
                    .create()
                    .show();
            return;
        }

        submitForm(finalDate, locId);
    }

    private void submitForm(LocalDate finalDate, int locId) {
        Instant i = Instant.from(finalDate.atStartOfDay().atZone(ZoneId.of("UTC")));
        Food f = foodViewModel.getFood().getValue();
        if (f != null) {
            LiveData<StatusCode> result = viewModel.addItem(f.id, locId, i, readScaledUnit());
            result.observe(this, this::maybeShowAddError);
        }
    }

    LocalDate readDateFromPicker() {
        return LocalDate.of(
                dateField.getYear(),
                dateField.getMonth()+1,
                dateField.getDayOfMonth());
    }

    int readLocation() {
        int position = locationField.getSelectedItemPosition();
        List<Location> locations = locationViewModel.getLocations().getValue();
        if (locations != null && position != -1 && position < locations.size()) {
            return locations.get(position).id;
        } else {
            return 0;
        }
    }

    int readScaledUnit() {
        int position = unitField.getSelectedItemPosition();
        List<ScaledUnitView> scaledUnits = scaledUnitViewModel.getUnits().getValue();
        if (scaledUnits != null && position != -1 && position < scaledUnits.size()) {
            return scaledUnits.get(position).id;
        } else {
            return 0;
        }
    }

    void hideConflictLabels() {
        setDateVisibility(View.GONE);
        setLocationVisibility(View.GONE);
        setUnitVisibility(View.GONE);
    }

    void setUnitVisibility(int visibility) {
        requireView().findViewById(R.id.fragment_add_food_item_unit_conflict).setVisibility(visibility);
    }

    void setLocationVisibility(int visibility) {
        requireView().findViewById(R.id.fragment_add_food_item_location_conflict).setVisibility(visibility);
    }

    void setDateVisibility(int visibility) {
        requireView().findViewById(R.id.fragment_add_food_item_date_conflict).setVisibility(visibility);
    }


}
