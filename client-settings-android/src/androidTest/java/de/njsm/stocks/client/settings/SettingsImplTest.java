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
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
    public void storingServerNameWorks() {
        String value = "test.example";

        uut.setServerName(value);

        assertThat(uut.getServerName(), is(value));
    }

    @Test
    public void storingCaPortWorks() {
        int value = 1409;

        uut.setCaPort(value);

        assertThat(uut.getCaPort(), is(value));
    }

    @Test
    public void storingRegistrationPortWorks() {
        int value = 1409;

        uut.setRegistrationPort(value);

        assertThat(uut.getRegistrationPort(), is(value));
    }

    @Test
    public void storingServerPortWorks() {
        int value = 1409;

        uut.setServerPort(value);

        assertThat(uut.getServerPort(), is(value));
    }

    @Test
    public void storingUserIdWorks() {
        int value = 1409;

        uut.setUserId(value);

        assertThat(uut.getUserId(), is(value));
    }

    @Test
    public void storingUserNameWorks() {
        String value = "username";

        uut.setUserName(value);

        assertThat(uut.getUserName(), is(value));
    }

    @Test
    public void storingUserDeviceIdWorks() {
        int value = 1409;

        uut.setUserDeviceId(value);

        assertThat(uut.getUserDeviceId(), is(value));
    }

    @Test
    public void storingUserDeviceNameWorks() {
        String value = "devicename";

        uut.setUserDeviceName(value);

        assertThat(uut.getUserDeviceName(), is(value));
    }

    @Test
    public void storingFingerprintWorks() {
        String value = "fingerprint";

        uut.setFingerprint(value);

        assertThat(uut.getFingerprint(), is(value));
    }

    @Test
    public void storingTicketWorks() {
        String value = "ticket";

        uut.setTicket(value);

        assertThat(uut.getTicket(), is(value));
    }
}
