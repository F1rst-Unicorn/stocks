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

package de.njsm.stocks.client.fragment.errordetails;

import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.ErrorDescription;
import de.njsm.stocks.client.business.entities.ErrorDetailsVisitor;
import de.njsm.stocks.client.business.entities.LocationAddForm;
import de.njsm.stocks.client.business.entities.SynchronisationErrorDetails;
import de.njsm.stocks.client.databind.ErrorDetailsHeadlineVisitor;
import de.njsm.stocks.client.databind.StatusCodeTranslator;
import de.njsm.stocks.client.fragment.InjectableFragment;
import de.njsm.stocks.client.navigation.ErrorDetailsNavigator;
import de.njsm.stocks.client.presenter.ErrorDetailsViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;

public class ErrorDetailsFragment extends InjectableFragment {

    private ErrorDetailsViewModel errorDetailsViewModel;

    private ErrorDetailsNavigator errorDetailsNavigator;

    private final StatusCodeTranslator statusCodeTranslator;

    private final ErrorDetailsPrinter errorDetailsPrinter;

    private final ErrorDetailsHeadlineVisitor errorDetailsHeadlineVisitor;

    public ErrorDetailsFragment() {
        statusCodeTranslator = new StatusCodeTranslator();
        errorDetailsPrinter = new ErrorDetailsPrinter();
        errorDetailsHeadlineVisitor = new ErrorDetailsHeadlineVisitor();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_error_details, container, false);

        long errorDescriptionId = errorDetailsNavigator.readArguments(getArguments());
        errorDetailsViewModel.getError(errorDescriptionId).observe(getViewLifecycleOwner(), this::bindData);

        setHasOptionsMenu(true);
        return result;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_error_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_error_details_retry) {
            errorDetailsViewModel.retry();
        } else if (item.getItemId() == R.id.menu_error_details_delete) {
            errorDetailsViewModel.delete();
        }
        errorDetailsNavigator.back();
        return true;
    }

    private void bindData(ErrorDescription errorDescription) {
        ((TextView) requireView().findViewById(R.id.fragment_error_details_data)).setText(errorDetailsPrinter.visit(errorDescription.errorDetails(), null));
        ((TextView) requireView().findViewById(R.id.fragment_error_details_error_message)).setText(errorDescription.errorMessage());
        ((TextView) requireView().findViewById(R.id.fragment_error_details_status_code)).setText(statusCodeTranslator.visit(errorDescription.statusCode(), null));
        ((TextView) requireView().findViewById(R.id.fragment_error_details_stacktrace)).setText(errorDescription.stackTrace());
        requireActivity().setTitle(errorDetailsHeadlineVisitor.visit(errorDescription.errorDetails(), null));
    }

    @Inject
    void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        errorDetailsViewModel = viewModelProvider.get(ErrorDetailsViewModel.class);
    }

    @Inject
    void setErrorDetailsNavigator(ErrorDetailsNavigator errorDetailsNavigator) {
        this.errorDetailsNavigator = errorDetailsNavigator;
    }

    private static final class ErrorDetailsPrinter implements ErrorDetailsVisitor<Void, String> {

        @Override
        public String locationAddForm(LocationAddForm locationAddForm, Void input) {
            return String.format("%1$s\n%2$s", locationAddForm.name(), locationAddForm.description());
        }

        @Override
        public String synchronisationErrorDetails(SynchronisationErrorDetails synchronisationErrorDetails, Void input) {
            return "";
        }
    }
}
