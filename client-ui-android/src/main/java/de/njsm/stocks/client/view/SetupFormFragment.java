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

package de.njsm.stocks.client.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.njsm.stocks.client.business.entities.RegistrationForm;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;

public class SetupFormFragment extends InjectableFragment {

    private SetupFormFragmentArgumentProvider argumentProvider;

    private RegistrationBackend registrationBackend;

    private SetupFormView view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_setup_form, container, false);
        result.findViewById(R.id.fragment_setup_form_button).setOnClickListener(this::onSubmitForm);
        view = new SetupFormView(result);
        return result;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        argumentProvider.visit(this, getArguments());
    }

    public void initialiseForm(RegistrationForm registrationForm) {
        view.initialiseForm(registrationForm);
    }

    private void onSubmitForm(View v) {
        registrationBackend.register(view.getFormData());
    }

    @Inject
    public void setArgumentProvider(SetupFormFragmentArgumentProvider argumentProvider) {
        this.argumentProvider = argumentProvider;
    }

    @Inject
    public void setRegistrationBackend(RegistrationBackend registrationBackend) {
        this.registrationBackend = registrationBackend;
    }
}
