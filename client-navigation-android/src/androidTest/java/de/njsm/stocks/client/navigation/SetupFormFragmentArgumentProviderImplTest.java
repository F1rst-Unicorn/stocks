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
import de.njsm.stocks.client.fragment.setupform.SetupFormFragment;
import de.njsm.stocks.client.fragment.setupform.SetupFormFragmentArgumentProvider;
import de.njsm.stocks.client.fragment.setupgreet.SetupGreetingFragmentDirections;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class SetupFormFragmentArgumentProviderImplTest {

    SetupFormFragmentArgumentProvider uut;

    @Before
    public void setUp() {
        uut = new SetupFormFragmentArgumentProviderImpl();
    }

    @Test
    public void visitingWithoutArgumentsDoesNothing() {
        SetupFormFragment fragment = Mockito.mock(SetupFormFragment.class);

        uut.visit(fragment, null);

        Mockito.verifyNoInteractions(fragment);
    }


    @Test
    public void visitingWithEmptyArgumentsDoesNothing() {
        SetupFormFragment fragment = Mockito.mock(SetupFormFragment.class);

        uut.visit(fragment, new Bundle());

        Mockito.verifyNoInteractions(fragment);
    }

    @Test
    public void visitingWithArgumentsPassesToFragment() {
        SetupFormFragment fragment = Mockito.mock(SetupFormFragment.class);
        RegistrationForm registrationForm = RegistrationForm.builder()
                .serverName("test.example")
                .caPort(1409)
                .registrationPort(1410)
                .serverPort(1411)
                .userId(1412)
                .userName("username")
                .userDeviceId(1412)
                .userDeviceName("userdevicename")
                .fingerprint("fingerprint")
                .ticket("ticket")
                .build();
        SetupGreetingFragmentDirections.ActionNavFragmentSetupGreetingToNavFragmentSetupForm direction =
                SetupGreetingFragmentDirections.actionNavFragmentSetupGreetingToNavFragmentSetupForm()
                        .setRegistrationForm(registrationForm);

        uut.visit(fragment, direction.getArguments());

        Mockito.verify(fragment).initialiseForm(registrationForm);
        Mockito.verifyNoMoreInteractions(fragment);
    }
}
