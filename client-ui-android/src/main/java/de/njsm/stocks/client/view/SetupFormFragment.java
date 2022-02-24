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
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.RegistrationForm;
import de.njsm.stocks.client.business.entities.SetupState;
import de.njsm.stocks.client.business.entities.visitor.SetupStateVisitor;
import de.njsm.stocks.client.navigation.SetupFormNavigator;
import de.njsm.stocks.client.presenter.SetupViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;

public class SetupFormFragment extends InjectableFragment {

    private SetupFormFragmentArgumentProvider argumentProvider;

    private SetupFormView view;

    private SetupViewModel setupViewModel;

    private SetupFormNavigator setupFormNavigator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_setup_form, container, false);
        view = new SetupFormView(result, this::getString);
        view.bindSubmitButton(this::onSubmitForm);
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
        setupViewModel.register(view.getFormData()).observe(getViewLifecycleOwner(), this::onStateUpdate);
    }

    private void onStateUpdate(SetupState setupState) {
        int message = setupState.visit(new SetupStateTranslator(), null);
        if (!setupState.isFinal()) {
            view.setProgressing(message);
        } else if (setupState.isSuccessful()) {
            setupFormNavigator.finishSetup();
        } else {
            view.setError(message);
        }
    }

    @Inject
    public void setArgumentProvider(SetupFormFragmentArgumentProvider argumentProvider) {
        this.argumentProvider = argumentProvider;
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        setupViewModel = viewModelProvider.get(SetupViewModel.class);
    }

    @Inject
    public void setSetupFragmentNavigator(SetupFormNavigator setupFormNavigator) {
        this.setupFormNavigator = setupFormNavigator;
    }

    private static class SetupStateTranslator implements SetupStateVisitor<Void, Integer> {

        @Override
        public Integer generatingKeys(SetupState setupState, Void input) {
            return R.string.dialog_generating_key;
        }

        @Override
        public Integer fetchingCertificate(SetupState setupState, Void input) {
            return R.string.dialog_fetching_certificate;
        }

        @Override
        public Integer verifyingCertificate(SetupState setupState, Void input) {
            return R.string.dialog_verifying_certificate;
        }

        @Override
        public Integer registeringKey(SetupState setupState, Void input) {
            return R.string.dialog_registering_key;
        }

        @Override
        public Integer storingSettings(SetupState setupState, Void input) {
            return R.string.dialog_storing_settings;
        }

        @Override
        public Integer generatingKeysFailed(SetupState setupState, Void input) {
            return R.string.dialog_generating_key_failed;
        }

        @Override
        public Integer fetchingCertificateFailed(SetupState setupState, Void input) {
            return R.string.dialog_fetching_certificate_failed;
        }

        @Override
        public Integer verifyingCertificateFailed(SetupState setupState, Void input) {
            return null;
        }

        @Override
        public Integer registeringKeyFailed(SetupState setupState, Void input) {
            return R.string.dialog_registering_key_failed;
        }

        @Override
        public Integer storingSettingsFailed(SetupState setupState, Void input) {
            return R.string.dialog_storing_settings_failed;
        }

        @Override
        public Integer success(SetupState setupState, Void input) {
            return R.string.dialog_success;
        }
    }
}
