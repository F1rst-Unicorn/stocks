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
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.entities.Location;
import de.njsm.stocks.android.db.views.FoodItemView;
import de.njsm.stocks.android.frontend.InjectedFragment;
import de.njsm.stocks.android.frontend.emptyfood.FoodViewModel;
import de.njsm.stocks.android.frontend.fooditem.FoodItemViewModel;
import de.njsm.stocks.android.frontend.locations.LocationViewModel;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.util.Logger;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.njsm.stocks.android.util.Utility.find;


public class EditItemFragment extends InjectedFragment {

    private static final Logger LOG = new Logger(EditItemFragment.class);

    private DatePicker date;

    private Spinner location;

    private FoodItemViewModel viewModel;

    private FoodViewModel foodViewModel;

    private ArrayAdapter<String> adapter;

    private LiveData<Food> food;

    private LiveData<FoodItemView> foodItem;

    private LiveData<List<Location>> locations;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_add_food_item, container, false);

        assert getArguments() != null;
        EditItemFragmentArgs input = EditItemFragmentArgs.fromBundle(getArguments());

        date = result.findViewById(R.id.fragment_add_food_item_date);
        LocalDate now = LocalDate.now();
        date.init(now.getYear(), now.getMonthValue() - 1, now.getDayOfMonth(), null);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FoodItemViewModel.class);
        foodViewModel = ViewModelProviders.of(this, viewModelFactory).get(FoodViewModel.class);
        LocationViewModel locationViewModel = ViewModelProviders.of(this, viewModelFactory).get(LocationViewModel.class);

        foodItem = viewModel.getItem(input.getFoodItemId());

        location = result.findViewById(R.id.fragment_add_food_item_spinner);
        adapter = new ArrayAdapter<>(requireActivity(),
                R.layout.item_location, R.id.item_location_name,
                new ArrayList<>());
        location.setAdapter(adapter);

        locations = locationViewModel.getLocations();
        locations.observe(this, l -> {
            List<String> data = l.stream().map(i -> i.name).collect(Collectors.toList());
            adapter.clear();
            adapter.addAll(data);
            adapter.notifyDataSetChanged();
            setDefaultLocation();
        });

        foodItem.observe(this, i -> {
            LocalDate date = LocalDate.from(i.getEatByDate().atZone(ZoneId.systemDefault()));
            this.date.init(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth(), null);
            setDefaultLocation();
        });

        foodItem.observe(this, i -> {
            food = foodViewModel.getFood(i.getOfType());
            food.observe(this, f -> {
                String title = getString(R.string.title_edit_item, f.name);
                requireActivity().setTitle(title);
                food.removeObservers(this);
            });
        });

        setHasOptionsMenu(true);
        return result;
    }

    private void setDefaultLocation() {
        List<Location> data = locations.getValue();
        FoodItemView foodItem = this.foodItem.getValue();
        if (foodItem != null && data != null) {
            find(foodItem.getStoredIn(), data).ifPresent(location::setSelection);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_edit_item_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        editItem();
        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                .navigateUp();
        return true;
    }

    private void editItem() {
        LOG.d("editing item");
        LocalDate finalDate = readDateFromPicker();
        int locId = readLocationId();

        sendItem(finalDate, locId);
        LOG.d("item sent");
    }

    private void sendItem(LocalDate finalDate, int locId) {
        Instant i = Instant.from(finalDate.atStartOfDay().atZone(ZoneId.of("UTC")));
        FoodItemView f = foodItem.getValue();
        if (f != null) {
            LiveData<StatusCode> result = viewModel.editItem(f.id, f.version, locId, i);
            result.observe(this, this::maybeShowEditError);
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
}
