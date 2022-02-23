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

package de.njsm.stocks.client.navigation;

import android.os.Bundle;
import de.njsm.stocks.client.business.entities.RegistrationForm;
import de.njsm.stocks.client.view.SetupFormFragment;
import de.njsm.stocks.client.view.SetupFormFragmentArgs;
import de.njsm.stocks.client.view.SetupFormFragmentArgumentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SetupFormFragmentArgumentProviderImpl implements SetupFormFragmentArgumentProvider {

    private static final Logger LOG = LoggerFactory.getLogger(SetupFormFragmentArgumentProviderImpl.class);

    @Override
    public void visit(SetupFormFragment fragment, Bundle bundle) {
        if (bundle == null)
            return;

        SetupFormFragmentArgs arguments = SetupFormFragmentArgs.fromBundle(bundle);
        if (!arguments.getServerName().isEmpty())
            initialiseForm(fragment, arguments);
    }

    private void initialiseForm(SetupFormFragment fragment, SetupFormFragmentArgs arguments) {
        RegistrationForm registrationForm = RegistrationForm.builder()
                .serverName(arguments.getServerName())
                .caPort(arguments.getCaPort())
                .registrationPort(arguments.getRegistrationPort())
                .serverPort(arguments.getServerPort())
                .userName(arguments.getUsername())
                .userId(arguments.getUserId())
                .userDeviceName(arguments.getDeviceName())
                .userDeviceId(arguments.getDeviceId())
                .ticket(arguments.getTicket())
                .fingerprint(arguments.getFingerprint())
                .build();
        fragment.initialiseForm(registrationForm);
    }
}
