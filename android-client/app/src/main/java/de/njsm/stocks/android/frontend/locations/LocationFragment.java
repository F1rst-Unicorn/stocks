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

package de.njsm.stocks.android.frontend.locations;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Location;
import de.njsm.stocks.android.frontend.InjectedFragment;
import de.njsm.stocks.android.frontend.interactor.LocationDeletionInteractor;
import de.njsm.stocks.android.frontend.interactor.LocationEditInteractor;
import de.njsm.stocks.android.frontend.util.NonEmptyValidator;
import de.njsm.stocks.common.api.StatusCode;

import java.util.List;

public class LocationFragment extends InjectedFragment {

    LocationViewModel viewModel;

    RecyclerView.Adapter<LocationAdapter.ViewHolder> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.template_swipe_list, container, false);

        result.findViewById(R.id.template_swipe_list_fab).setOnClickListener(this::addLocation);
        RecyclerView list = result.findViewById(R.id.template_swipe_list_list);
        list.setLayoutManager(new LinearLayoutManager(requireActivity()));

        viewModel = getViewModelProvider().get(LocationViewModel.class);

        LocationEditInteractor editor = new LocationEditInteractor(
                this,
                viewModel::renameLocation,
                viewModel::getLocation
        );

        adapter = new LocationAdapter(viewModel.getLocations(),
                this::showContainedFood,
                v -> this.editInternally(v,
                        viewModel.getLocations(),
                        R.string.dialog_rename_location,
                        editor::observeEditing));
        viewModel.getLocations().observe(getViewLifecycleOwner(), u -> adapter.notifyDataSetChanged());
        list.setAdapter(adapter);

        LocationDeletionInteractor interactor = new LocationDeletionInteractor(
                this, result,
                i -> adapter.notifyDataSetChanged(),
                i -> viewModel.deleteLocation(i, false),
                i -> viewModel.getLocation(i),
                i -> viewModel.deleteLocation(i, true));
        addSwipeToDelete(list, viewModel.getLocations(), interactor::initiateDeletion);

        initialiseSwipeRefresh(result, viewModelFactory);

        return result;
    }

    private void addLocation(View view) {
        EditText textField = (EditText) getLayoutInflater().inflate(R.layout.text_field, null);
        textField.addTextChangedListener(
                new NonEmptyValidator(textField, this::showEmptyInputError));
        textField.setHint(getResources().getString(R.string.hint_location));
        new AlertDialog.Builder(requireActivity())
                .setTitle(getResources().getString(R.string.dialog_new_location))
                .setView(textField)
                .setPositiveButton(getResources().getString(R.string.dialog_ok), (dialog, whichButton) -> {
                    String name = textField.getText().toString().trim();
                    LiveData<StatusCode> result = viewModel.addLocation(name);
                    result.observe(this, this::maybeShowAddError);
                })
                .setNegativeButton(getResources().getString(android.R.string.cancel), this::doNothing)
                .show();
    }

    private void showContainedFood(View view) {
        LocationAdapter.ViewHolder holder = (LocationAdapter.ViewHolder) view.getTag();
        int position = holder.getAdapterPosition();
        List<Location> data = viewModel.getLocations().getValue();
        if (data != null) {
            int id = data.get(position).id;
            LocationFragmentDirections.ActionNavFragmentLocationsToNavFragmentFood args =
                    LocationFragmentDirections.actionNavFragmentLocationsToNavFragmentFood()
                    .setLocation(id);
            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                    .navigate(args);
        }
    }
}
