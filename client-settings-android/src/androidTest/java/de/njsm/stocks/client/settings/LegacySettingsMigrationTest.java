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

import static de.njsm.stocks.client.settings.SettingsImpl.*;
import static de.njsm.stocks.client.settings.SettingsImplTest.getRegistrationForm;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class LegacySettingsMigrationTest {

    private SharedPreferences sharedPreferences;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        RegistrationForm expected = getRegistrationForm();

        sharedPreferences = context.getSharedPreferences("test", Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(LEGACY_SERVER_NAME_CONFIG, expected.serverName())
                .putInt(LEGACY_CA_PORT_CONFIG, expected.caPort())
                .putInt(LEGACY_SENTRY_PORT_CONFIG, expected.registrationPort())
                .putInt(LEGACY_SERVER_PORT_CONFIG, expected.serverPort())
                .putString(LEGACY_USERNAME_CONFIG, expected.userName())
                .putString(LEGACY_DEVICE_NAME_CONFIG, expected.userDeviceName())
                .putInt(LEGACY_UID_CONFIG, expected.userId())
                .putInt(LEGACY_DID_CONFIG, expected.userDeviceId())
                .putString(LEGACY_FPR_CONFIG, expected.fingerprint())
                .putString(LEGACY_TICKET_CONFIG, expected.ticket())
                .commit();
    }

    @Test
    public void migrationWorks() {
        RegistrationForm expected = getRegistrationForm();

        SettingsImpl uut = new SettingsImpl(sharedPreferences);

        assertThat(uut.getServerName(), is(expected.serverName()));
        assertThat(uut.getCaPort(), is(expected.caPort()));
        assertThat(uut.getRegistrationPort(), is(expected.registrationPort()));
        assertThat(uut.getServerPort(), is(expected.serverPort()));
        assertThat(uut.getUserId(), is(expected.userId()));
        assertThat(uut.getUserName(), is(expected.userName()));
        assertThat(uut.getUserDeviceId(), is(expected.userDeviceId()));
        assertThat(uut.getUserDeviceName(), is(expected.userDeviceName()));
        assertThat(uut.getFingerprint(), is(expected.fingerprint()));
        assertThat(uut.getTicket(), is(expected.ticket()));
    }
}
