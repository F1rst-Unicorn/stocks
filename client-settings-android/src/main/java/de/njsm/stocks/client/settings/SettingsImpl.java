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

import android.content.SharedPreferences;
import de.njsm.stocks.client.business.Settings;
import de.njsm.stocks.client.business.SettingsWriter;
import de.njsm.stocks.client.business.SetupStatusChecker;
import de.njsm.stocks.client.business.entities.RegistrationForm;

import javax.inject.Inject;

class SettingsImpl implements Settings, SettingsWriter, SetupStatusChecker {

    private static final String SERVER_NAME_KEY = "de.njsm.stocks.client.settings.SettingsImpl.serverName";
    private static final String CA_PORT_KEY = "de.njsm.stocks.client.settings.SettingsImpl.caPort";
    private static final String REGISTRATION_PORT_KEY = "de.njsm.stocks.client.settings.SettingsImpl.registrationPort";
    private static final String SERVER_PORT_KEY = "de.njsm.stocks.client.settings.SettingsImpl.serverPort";
    private static final String USER_ID_KEY = "de.njsm.stocks.client.settings.SettingsImpl.userId";
    private static final String USER_NAME_KEY = "de.njsm.stocks.client.settings.SettingsImpl.userName";
    private static final String USER_DEVICE_ID_KEY = "de.njsm.stocks.client.settings.SettingsImpl.userDeviceId";
    private static final String USER_DEVICE_NAME_KEY = "de.njsm.stocks.client.settings.SettingsImpl.userDeviceName";
    private static final String FINGERPRINT_KEY = "de.njsm.stocks.client.settings.SettingsImpl.fingerprint";
    private static final String TICKET_KEY = "de.njsm.stocks.client.settings.SettingsImpl.ticket";

    private final SharedPreferences sharedPreferences;

    @Inject
    SettingsImpl(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void store(RegistrationForm form) {
        sharedPreferences.edit()
                .putString(SERVER_NAME_KEY, form.serverName())
                .putInt(CA_PORT_KEY, form.caPort())
                .putInt(REGISTRATION_PORT_KEY, form.registrationPort())
                .putInt(SERVER_PORT_KEY, form.serverPort())
                .putInt(USER_ID_KEY, form.userId())
                .putString(USER_NAME_KEY, form.userName())
                .putInt(USER_DEVICE_ID_KEY, form.userDeviceId())
                .putString(USER_DEVICE_NAME_KEY, form.userDeviceName())
                .putString(FINGERPRINT_KEY, form.fingerprint())
                .putString(TICKET_KEY, form.ticket())
                .apply();
    }

    @Override
    public String getServerName() {
        return sharedPreferences.getString(SERVER_NAME_KEY, "");
    }

    @Override
    public int getCaPort() {
        return sharedPreferences.getInt(CA_PORT_KEY, 0);
    }

    @Override
    public int getRegistrationPort() {
        return sharedPreferences.getInt(REGISTRATION_PORT_KEY, 0);
    }

    @Override
    public int getServerPort() {
        return sharedPreferences.getInt(SERVER_PORT_KEY, 0);
    }

    @Override
    public int getUserId() {
        return sharedPreferences.getInt(USER_ID_KEY, 0);
    }

    @Override
    public String getUserName() {
        return sharedPreferences.getString(USER_NAME_KEY, "");
    }

    @Override
    public int getUserDeviceId() {
        return sharedPreferences.getInt(USER_DEVICE_ID_KEY, 0);
    }

    @Override
    public String getUserDeviceName() {
        return sharedPreferences.getString(USER_DEVICE_NAME_KEY, "");
    }

    @Override
    public String getFingerprint() {
        return sharedPreferences.getString(FINGERPRINT_KEY, "");
    }

    @Override
    public String getTicket() {
        return sharedPreferences.getString(TICKET_KEY, "");
    }

    @Override
    public boolean isSetup() {
        return !getServerName().isEmpty();
    }
}
