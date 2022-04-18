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

package de.njsm.stocks.client.fragment.locationedit;

import android.os.Bundle;
import android.view.*;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.textfield.TextInputLayout;
import de.njsm.stocks.client.business.entities.Identifiable;
import de.njsm.stocks.client.business.entities.Location;
import de.njsm.stocks.client.business.entities.LocationToEdit;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.navigation.LocationEditNavigator;
import de.njsm.stocks.client.presenter.LocationEditViewModel;
import de.njsm.stocks.client.ui.R;
import de.njsm.stocks.client.util.NonEmptyValidator;

import javax.inject.Inject;

import static de.njsm.stocks.client.fragment.view.ViewUtility.setText;
import static de.njsm.stocks.client.fragment.view.ViewUtility.stringFromForm;

public class LocationEditFragment extends BottomToolbarFragment {

    private LocationEditViewModel locationViewModel;

    private LocationEditNavigator locationEditNavigator;

    private TextInputLayout nameField;

    private TextInputLayout descriptionField;

    private Identifiable<Location> id;

    private boolean maySubmit = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        View result = insertContent(inflater, root, R.layout.fragment_location_form);
        descriptionField = result.findViewById(R.id.fragment_location_form_description);
        nameField = result.findViewById(R.id.fragment_location_form_name);
        EditText editText = nameField.getEditText();
        if (editText != null)
            editText.addTextChangedListener(new NonEmptyValidator(nameField, this::onNameChanged));

        int rawId = locationEditNavigator.getLocationId(requireArguments());
        this.id = () -> rawId;
        locationViewModel.get(id).observe(getViewLifecycleOwner(), v -> {
            setText(nameField, v.name());
            setText(descriptionField, v.description());
        });

        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.check, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (!maySubmit) {
            nameField.setError(getString(R.string.error_may_not_be_empty));
            return true;
        }

        LocationToEdit form = LocationToEdit.builder()
                .id(id.id())
                .name(stringFromForm(nameField))
                .description(stringFromForm(requireView().findViewById(R.id.fragment_location_form_description)))
                .build();
        locationViewModel.editLocation(form);
        locationEditNavigator.back();
        return true;
    }

    private void onNameChanged(TextInputLayout textInputLayout, Boolean isEmpty) {
        maySubmit = !isEmpty;
        if (isEmpty)
            textInputLayout.setError(getString(R.string.error_may_not_be_empty));
        else
            textInputLayout.setError(null);
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        locationViewModel = viewModelProvider.get(LocationEditViewModel.class);
    }

    @Inject
    public void setNavigator(LocationEditNavigator locationEditNavigator) {
        this.locationEditNavigator = locationEditNavigator;
    }
}
