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

package de.njsm.stocks.client.settings;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.test.core.app.ApplicationProvider;
import de.njsm.stocks.client.business.entities.RegistrationForm;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SettingsImplTest {

    private SettingsImpl uut;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("test", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
        uut = new SettingsImpl(sharedPreferences);
    }

    @Test
    public void initiallyEmpty() {
        assertThat(uut.getServerName(), is(""));
        assertThat(uut.getCaPort(), is(0));
        assertThat(uut.getRegistrationPort(), is(0));
        assertThat(uut.getServerPort(), is(0));
        assertThat(uut.getUserId(), is(0));
        assertThat(uut.getUserName(), is(""));
        assertThat(uut.getUserDeviceId(), is(0));
        assertThat(uut.getUserDeviceName(), is(""));
        assertThat(uut.getFingerprint(), is(""));
        assertThat(uut.getTicket(), is(""));
    }

    @Test
    public void storingWorks() {
        RegistrationForm form = getRegistrationForm();

        uut.store(form);

        assertThat(uut.getServerName(), is(form.serverName()));
        assertThat(uut.getCaPort(), is(form.caPort()));
        assertThat(uut.getRegistrationPort(), is(form.registrationPort()));
        assertThat(uut.getServerPort(), is(form.serverPort()));
        assertThat(uut.getUserId(), is(form.userId()));
        assertThat(uut.getUserName(), is(form.userName()));
        assertThat(uut.getUserDeviceId(), is(form.userDeviceId()));
        assertThat(uut.getUserDeviceName(), is(form.userDeviceName()));
        assertThat(uut.getFingerprint(), is(form.fingerprint()));
        assertThat(uut.getTicket(), is(form.ticket()));
    }

    @Test
    public void emptySettingsAreNotSetup() {
        assertFalse(uut.isSetup());
    }

    @Test
    public void presentSettingsAreSetup() {
        uut.store(getRegistrationForm());

        assertTrue(uut.isSetup());
    }

    static RegistrationForm getRegistrationForm() {
        return RegistrationForm.builder()
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
    }
}
