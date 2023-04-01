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

package de.njsm.stocks.client.fragment.ticketshow;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.RegistrationForm;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.navigation.TicketShowNavigator;
import de.njsm.stocks.client.presenter.TicketShowViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;

public class TicketShowFragment extends BottomToolbarFragment {

    private TicketShowViewModel viewModel;

    private TicketShowNavigator navigator;

    private TicketShowForm form;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        View result = insertContent(inflater, root, R.layout.fragment_ticket_show);
        form = new TicketShowForm(result, this::getString);
        viewModel.getData(navigator.getId(requireArguments())).observe(getViewLifecycleOwner(), this::displayTicket);
        form.onShare(this::onShare);
        return root;
    }

    private void displayTicket(RegistrationForm registrationForm) {
        DisplayMetrics display = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(display);
        form.showData(registrationForm, display.widthPixels);
    }

    private void onShare(View view) {
        viewModel.getData(navigator.getId(requireArguments())).observe(getViewLifecycleOwner(), form -> {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_SEND);
            i.putExtra(Intent.EXTRA_TEXT, form.toQrString());
            i.setType("text/plain");
            startActivity(i);
        });
    }

    @Inject
    @Override
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        viewModel = viewModelProvider.get(TicketShowViewModel.class);
    }

    @Inject
    void setNavigator(TicketShowNavigator navigator) {
        this.navigator = navigator;
    }
}
