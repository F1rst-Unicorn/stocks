/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.client.fragment.locationconflict;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.client.business.entities.Location;
import de.njsm.stocks.client.business.entities.LocationEditFormData;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.fragment.view.LocationForm;
import de.njsm.stocks.client.navigation.LocationConflictNavigator;
import de.njsm.stocks.client.presenter.LocationConflictViewModel;
import de.njsm.stocks.client.ui.R;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import static de.njsm.stocks.client.business.entities.Versionable.INVALID_VERSION;

public class LocationConflictFragment extends BottomToolbarFragment implements MenuProvider {

    private LocationConflictViewModel locationViewModel;

    private LocationConflictNavigator locationConflictNavigator;

    private LocationForm form;

    private IdImpl<Location> id;

    private int version = INVALID_VERSION;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        this.form = new LocationForm(insertContent(inflater, root, R.layout.fragment_location_form), this::getString);

        long errorId = locationConflictNavigator.getErrorId(requireArguments());
        locationViewModel.getLocationEditConflict(errorId).observe(getViewLifecycleOwner(), v -> {
            id = v.id();
            version = v.remoteVersion();
            form.setName(v.name().suggestedValue());
            form.setDescription(String.format(v.description().suggestedValue(),
                    getString(R.string.hint_original),
                    getString(R.string.hint_remote),
                    getString(R.string.hint_local)
            ));

            if (v.hasNoConflict()) {
                submit();
                return;
            }

            if (v.name().needsHandling()) {
                form.showNameConflict(v.name());
            } else {
                form.hideName();
            }

            if (!v.description().needsHandling()) {
                form.hideDescription();
            }
        });

        requireActivity().addMenuProvider(this, getViewLifecycleOwner());
        return root;
    }

    @Override
    public void onCreateMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.check, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull @NotNull MenuItem item) {
        submit();
        return true;
    }

    private void submit() {
        if (!form.maySubmit()) {
            form.setNameError(R.string.error_may_not_be_empty);
            return;
        }

        LocationEditFormData data = LocationEditFormData.create(
                id,
                version,
                form.getName(),
                form.getDescription());
        locationViewModel.editLocation(data);
        locationConflictNavigator.back();
    }

    @Inject
    @Override
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        locationViewModel = viewModelProvider.get(LocationConflictViewModel.class);
    }

    @Inject
    void setLocationConflictNavigator(LocationConflictNavigator locationConflictNavigator) {
        this.locationConflictNavigator = locationConflictNavigator;
    }
}
