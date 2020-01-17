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

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.Period;
import org.threeten.bp.ZoneId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.entities.Location;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.emptyfood.FoodViewModel;
import de.njsm.stocks.android.frontend.fooditem.FoodItemViewModel;
import de.njsm.stocks.android.frontend.locations.LocationViewModel;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.util.Logger;


public class AddItemFragment extends BaseFragment {

    private static final Logger LOG = new Logger(AddItemFragment.class);

    private DatePicker date;

    private Spinner location;

    private ViewModelProvider.Factory viewModelFactory;

    private FoodItemViewModel viewModel;

    private FoodViewModel foodViewModel;

    private LocationViewModel locationViewModel;

    private ArrayAdapter<String> adapter;

    private LiveData<Food> food;

    private LiveData<List<Location>> locations;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_add_food_item, container, false);

        assert getArguments() != null;
        AddItemFragmentArgs input = AddItemFragmentArgs.fromBundle(getArguments());

        date = result.findViewById(R.id.fragment_add_food_item_date);
        LocalDate now = LocalDate.now();
        initialiseDatePicker(now);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FoodItemViewModel.class);
        foodViewModel = ViewModelProviders.of(this, viewModelFactory).get(FoodViewModel.class);
        locationViewModel = ViewModelProviders.of(this, viewModelFactory).get(LocationViewModel.class);

        location = result.findViewById(R.id.fragment_add_food_item_spinner);
        adapter = new ArrayAdapter<>(requireActivity(),
                R.layout.item_location, R.id.item_location_name,
                new ArrayList<>());
        location.setAdapter(adapter);

        locations = locationViewModel.getLocations();
        food = foodViewModel.getFood(input.getFoodId());
        food.observe(this, f -> {
            locations.observe(this, l -> {
                Food food = this.food.getValue();
                LiveData<Location> defaultLocation;
                if (food != null && food.location != 0) {
                    defaultLocation = locationViewModel.getLocation(food.location);
                } else {
                    defaultLocation = locationViewModel.getLocationWithMostItemsOfType(input.getFoodId());
                }
                defaultLocation.observe(this, this::setDefaultLocation);
                List<String> data = l.stream().map(i -> i.name).collect(Collectors.toList());
                adapter.clear();
                adapter.addAll(data);
                adapter.notifyDataSetChanged();

            });
            String title = getString(R.string.title_add_item, f.name);
            requireActivity().setTitle(title);
            if (f.expirationOffset != 0) {
                initialiseDatePicker(now.plus(Period.ofDays(f.expirationOffset)));
            } else {
                initialiseDatePickerFromExistingFood(input);
            }
            food.removeObservers(this);
        });

        setHasOptionsMenu(true);
        return result;
    }

    private void initialiseDatePickerFromExistingFood(AddItemFragmentArgs input) {
        LiveData<Instant> latestExpiration = viewModel.getLatestExpirationOf(input.getFoodId());
        latestExpiration.observe(this, i -> {
            if (i != null) {
                LocalDate date = LocalDate.from(i.atZone(ZoneId.systemDefault()));
                initialiseDatePicker(date);
                latestExpiration.removeObservers(this);
            }
        });
    }

    private void initialiseDatePicker(LocalDate date) {
        this.date.init(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth(), null);
    }

    private void setDefaultLocation(Location l) {
        List<Location> data = locations.getValue();
        if (l != null && data != null) {
            int position = data.indexOf(l);
            location.setSelection(position);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_add_item_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fragment_add_item_options_done:
                addItem();
                Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                        .navigateUp();
                break;
            case R.id.fragment_add_item_options_add_more:
                addItem();
                break;
        }
        return true;
    }

    private void addItem() {
        LOG.d("adding new item");
        LocalDate finalDate = readDateFromPicker();
        int locId = readLocationId();

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
        }

        sendItem(finalDate, locId);
        LOG.d("item sent");
    }

    private void sendItem(LocalDate finalDate, int locId) {
        Instant i = Instant.from(finalDate.atStartOfDay().atZone(ZoneId.of("UTC")));
        Food f = food.getValue();
        if (f != null) {
            LiveData<StatusCode> result = viewModel.addItem(f.id, locId, i);
            result.observe(this, this::maybeShowAddError);
        }
    }

    private LocalDate readDateFromPicker() {
        return LocalDate.of(
                date.getYear(),
                date.getMonth()+1,
                date.getDayOfMonth());
    }

    private int readLocationId() {
        int position = location.getSelectedItemPosition();
        List<Location> data = locations.getValue();
        if (position == -1 || data == null || data.size() <= position)
            return 0;
        else
            return data.get(position).id;
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }
}
