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

package de.njsm.stocks.android.frontend.fooditem;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.entities.Location;
import de.njsm.stocks.android.db.views.FoodItemView;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.eannumber.EanNumberViewModel;
import de.njsm.stocks.android.frontend.emptyfood.FoodViewModel;
import de.njsm.stocks.android.frontend.interactor.FoodItemDeletionInteractor;
import de.njsm.stocks.android.frontend.locations.LocationViewModel;
import de.njsm.stocks.android.network.server.StatusCode;

public class FoodItemFragment extends BaseFragment {

    private FoodItemViewModel viewModel;

    private FoodViewModel foodViewModel;

    private EanNumberViewModel eanNumberViewModel;

    private ViewModelProvider.Factory viewModelFactory;

    private FoodItemAdapter adapter;

    private FoodItemFragmentArgs input;

    @Override
    public void onAttach(@NonNull Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.template_swipe_list, container, false);

        assert getArguments() != null;
        input = FoodItemFragmentArgs.fromBundle(getArguments());

        result.findViewById(R.id.template_swipe_list_fab).setOnClickListener(this::addItem);
        RecyclerView list = result.findViewById(R.id.template_swipe_list_list);
        list.setLayoutManager(new LinearLayoutManager(requireActivity()));

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FoodItemViewModel.class);
        eanNumberViewModel = ViewModelProviders.of(this, viewModelFactory).get(EanNumberViewModel.class);
        viewModel.init(input.getFoodId());
        foodViewModel = ViewModelProviders.of(this, viewModelFactory).get(FoodViewModel.class);
        foodViewModel.initFood(input.getFoodId());
        foodViewModel.getFood().observe(getViewLifecycleOwner(), u -> requireActivity().setTitle(u == null ? "" : u.name));
        foodViewModel.getFood().observe(getViewLifecycleOwner(), u -> requireActivity().invalidateOptionsMenu());

        adapter = new FoodItemAdapter(getResources(), requireActivity().getTheme(),
                viewModel.getFoodItems(), this::editItem);
        viewModel.getFoodItems().observe(getViewLifecycleOwner(), v -> adapter.notifyDataSetChanged());
        list.setAdapter(adapter);

        FoodItemDeletionInteractor interactor = new FoodItemDeletionInteractor(
                this,
                i -> viewModel.deleteItem(i),
                i -> viewModel.getItem(i));
        addSwipeToDelete(list, viewModel.getFoodItems(), R.drawable.ic_local_dining_white_24dp, interactor::initiateDeletion);

        setHasOptionsMenu(true);
        initialiseSwipeRefresh(result, viewModelFactory);
        maybeAddEanCode(input.getEanNumber());
        return result;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_food_item_options, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.fragment_food_item_options_shopping);
        Food f = foodViewModel.getFood().getValue();
        if (f != null) {
            if (f.toBuy) {
                item.setIcon(R.drawable.ic_remove_shopping_cart_white_24);
                item.setTitle(R.string.title_remove_from_cart);
            } else {
                item.setIcon(R.drawable.ic_add_shopping_cart_white_24);
                item.setTitle(R.string.title_add_to_cart);
            }
        }
    }

    private void maybeAddEanCode(String eanNumber) {
        if (eanNumber != null && ! eanNumber.isEmpty()) {
            eanNumberViewModel.addEanNumber(eanNumber, input.getFoodId())
                    .observe(getViewLifecycleOwner(), this::maybeShowAddError);
        }
        getArguments().remove("eanNumber");
    }

    private void addItem(View view) {
        FoodItemFragmentDirections.ActionNavFragmentFoodItemToNavFragmentAddFoodItem args =
                FoodItemFragmentDirections.actionNavFragmentFoodItemToNavFragmentAddFoodItem(input.getFoodId());
        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                .navigate(args);
    }

    private void editItem(View view) {
        FoodItemAdapter.ViewHolder holder = (FoodItemAdapter.ViewHolder) view.getTag();
        int position = holder.getAdapterPosition();
        List<FoodItemView> data = viewModel.getFoodItems().getValue();
        if (data != null) {
            int id = data.get(position).id;

            FoodItemFragmentDirections.ActionNavFragmentFoodItemToNavFragmentEditFoodItem args =
                    FoodItemFragmentDirections.actionNavFragmentFoodItemToNavFragmentEditFoodItem(id);
            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                    .navigate(args);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fragment_food_item_options_ean:
                FoodItemFragmentDirections.ActionNavFragmentFoodItemToNavFragmentEanNumber args =
                        FoodItemFragmentDirections.actionNavFragmentFoodItemToNavFragmentEanNumber(input.getFoodId());
                Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                        .navigate(args);
                break;
            case R.id.fragment_food_item_options_shopping:
                Food f = foodViewModel.getFood().getValue();
                if (f != null) {
                    LiveData<StatusCode> code = foodViewModel.setToBuyStatus(f, ! f.toBuy);
                    code.observe(this, c -> {
                        code.removeObservers(this);
                        requireActivity().invalidateOptionsMenu();
                    });
                }
                break;
            case R.id.fragment_food_item_options_expiration_offset:
                editExpirationDate();
                break;
            case R.id.fragment_food_item_options_location:
                editDefaultLocation();
                break;
            case R.id.fragment_food_item_options_events:
                goToEvents();
                break;
            case R.id.fragment_food_item_options_charts:
                goToCharts();
                break;
        }
        return true;
    }

    private void goToCharts() {
        FoodItemFragmentDirections.ActionNavFragmentFoodItemToNavFragmentFoodChart args =
                FoodItemFragmentDirections.actionNavFragmentFoodItemToNavFragmentFoodChart(input.getFoodId());
        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                .navigate(args);
    }

    private void goToEvents() {
        FoodItemFragmentDirections.ActionNavFragmentFoodItemToNavFragmentFoodItemHistory args =
                FoodItemFragmentDirections.actionNavFragmentFoodItemToNavFragmentFoodItemHistory(input.getFoodId());
        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                .navigate(args);
    }

    private void editDefaultLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LocationViewModel locationViewModel = ViewModelProviders.of(this, viewModelFactory).get(LocationViewModel.class);
        LiveData<List<Location>> locationData = locationViewModel.getLocations();
        Spinner spinner = (Spinner) getLayoutInflater().inflate(R.layout.spinner, null);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(),
                R.layout.item_location, R.id.item_location_name,
                new ArrayList<>());
        spinner.setAdapter(adapter);

        locationData.observe(this, l -> {
            List<String> data = l.stream().map(i -> i.name).collect(Collectors.toList());
            adapter.clear();
            adapter.addAll(data);
            adapter.notifyDataSetChanged();

            Food food = foodViewModel.getFood().getValue();
            if (food != null && food.location != 0) {
                LiveData<Location> liveData = locationViewModel.getLocation(food.location);
                liveData.observe(this, loc -> {
                    int position = l.indexOf(loc);
                    if (position != -1)
                        spinner.setSelection(position);
                    liveData.removeObservers(this);
                });
            }
        });

        builder
                .setTitle(getString(R.string.dialog_default_location))
                .setView(spinner)
                .setPositiveButton(R.string.dialog_ok, (dialog, whichButton) -> {
                    Food food = foodViewModel.getFood().getValue();
                    List<Location> locations = locationData.getValue();
                    int position = spinner.getSelectedItemPosition();
                    if (food != null
                            && locations != null
                            && position >= 0
                            && position < locations.size()
                            && locations.get(position).id != food.location) {
                        LiveData<StatusCode> result = foodViewModel.setFoodDefaultLocation(food, locations.get(position).id);
                        result.observe(this, this::maybeShowEditError);
                    }
                })
                .setNeutralButton(R.string.dialog_remove, (dialog, whichButton) -> {
                    Food food = foodViewModel.getFood().getValue();
                    if (food != null && food.location != 0) {
                        LiveData<StatusCode> result = foodViewModel.setFoodDefaultLocation(food, 0);
                        result.observe(this, this::maybeShowEditError);
                    }
                })
                .setNegativeButton(android.R.string.cancel, (d, b) -> {})
                .show();
    }

    private void editExpirationDate() {
        NumberPicker view = (NumberPicker) getLayoutInflater().inflate(R.layout.number_picker, null);
        view.setMinValue(0);
        view.setMaxValue(Integer.MAX_VALUE);
        view.setWrapSelectorWheel(false);
        Food f = foodViewModel.getFood().getValue();
        if (f != null) {
            view.setValue(f.expirationOffset);
        } else {
            view.setValue(0);
        }
        new AlertDialog.Builder(requireActivity())
                .setTitle(getString(R.string.dialog_default_expiration_offset))
                .setView(view)
                .setPositiveButton(R.string.dialog_ok, (dialog, whichButton) -> {
                    Food food = foodViewModel.getFood().getValue();
                    int newOffset = view.getValue();
                    if (food != null && food.expirationOffset != newOffset) {
                        LiveData<StatusCode> result = foodViewModel.setFoodExpirationOffset(food, newOffset);
                        result.observe(this, this::maybeShowEditError);
                    }
                })
                .setNegativeButton(android.R.string.cancel, (d, b) -> {})
                .show();
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }
}
