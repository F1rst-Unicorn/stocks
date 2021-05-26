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

package de.njsm.stocks.android.frontend.editdescription;

import android.os.Bundle;
import android.view.*;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Location;
import de.njsm.stocks.android.frontend.InjectedFragment;
import de.njsm.stocks.android.frontend.locations.LocationViewModel;
import de.njsm.stocks.android.network.server.StatusCode;

public class LocationEditDescriptionFragment extends InjectedFragment {

    private LocationViewModel locationViewModel;

    private EditText editText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_edit_description, container, false);

        assert getArguments() != null;
        LocationEditDescriptionFragmentArgs input = LocationEditDescriptionFragmentArgs.fromBundle(getArguments());

        editText = result.findViewById(R.id.fragment_edit_description_text);

        locationViewModel = ViewModelProviders.of(this, viewModelFactory).get(LocationViewModel.class);
        locationViewModel.init(input.getLocationId());
        locationViewModel.getPreparedLocation().observe(getViewLifecycleOwner(), f -> {
            locationViewModel.getPreparedLocation().removeObservers(getViewLifecycleOwner());
            editText.setText(f.description);
            editText.setSelection(f.description.length());
        });

        setHasOptionsMenu(true);
        return result;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_edit_description_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String text = editText.getText().toString().trim();

        locationViewModel.getPreparedLocation().removeObservers(getViewLifecycleOwner());
        locationViewModel.getPreparedLocation().observe(getViewLifecycleOwner(), l -> {
            locationViewModel.getPreparedLocation().removeObservers(getViewLifecycleOwner());
            setDescription(text, l);
        });

        return true;
    }

    private void setDescription(String text, Location location) {
        if (text.equals(location.description)) {
            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                    .navigateUp();
            return;
        }

        LiveData<StatusCode> result = locationViewModel.setLocationDescription(location.id, location.version, text);
        result.observe(getViewLifecycleOwner(), code -> {
            result.removeObservers(getViewLifecycleOwner());

            if (code == StatusCode.SUCCESS) {
                Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                        .navigateUp();

            } else if (code == StatusCode.INVALID_DATA_VERSION) {
                locationViewModel.getPreparedLocation().observe(getViewLifecycleOwner(), newLocation -> {
                    if (newLocation.version != location.version) {
                        locationViewModel.getPreparedLocation().removeObservers(getViewLifecycleOwner());
                        if (!location.description.equals(newLocation.description)) {
                            String newText = String.format("%s:\n%s\n\n%s:\n%s\n\n%s:\n%s",
                                    getString(R.string.hint_original),
                                    location.description,
                                    getString(R.string.hint_local),
                                    text,
                                    getString(R.string.hint_remote),
                                    newLocation.description);
                            editText.setText(newText);
                            editText.setSelection(newText.length());
                            showErrorMessage(requireActivity(), R.string.dialog_conflicting_description);
                        } else {
                            setDescription(text, newLocation);
                        }
                    }
                });

            } else {
                showErrorMessage(requireActivity(), code.getEditErrorMessage());
            }
        });
    }
}
