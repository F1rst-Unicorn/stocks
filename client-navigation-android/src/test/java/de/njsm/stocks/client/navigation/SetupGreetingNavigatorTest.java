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

import de.njsm.stocks.client.business.entities.RegistrationForm;
import de.njsm.stocks.client.view.SetupGreetingFragmentDirections;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;

public class SetupGreetingNavigatorTest extends NavigationTest {

    private SetupGreetingNavigator uut;

    @Before
    public void setUp() {
        uut = new SetupGreetingNavigatorImpl(navigationArgConsumer);
    }

    @Test
    public void registeringManuallyBindsCorrectly() {
        uut.registerManually();

        SetupGreetingFragmentDirections.ActionNavFragmentSetupGreetingToNavFragmentSetupForm actual = navigationArgConsumer.getLastArgument(SetupGreetingFragmentDirections.ActionNavFragmentSetupGreetingToNavFragmentSetupForm.class);
        assertThat(actual.getActionId(), is(R.id.action_nav_fragment_setup_greeting_to_nav_fragment_setup_form));
        assertNull(actual.getRegistrationForm());
    }

    @Test
    public void registeringWithDataBindsCorrectly() {
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

        uut.registerWithPrefilledData(registrationForm);

        SetupGreetingFragmentDirections.ActionNavFragmentSetupGreetingToNavFragmentSetupForm actual =
                navigationArgConsumer.getLastArgument(SetupGreetingFragmentDirections.ActionNavFragmentSetupGreetingToNavFragmentSetupForm.class);
        assertThat(actual.getRegistrationForm(), is(registrationForm));
    }
}
