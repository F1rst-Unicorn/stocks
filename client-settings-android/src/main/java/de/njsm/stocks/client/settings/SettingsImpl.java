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

import javax.inject.Inject;

class SettingsImpl implements Settings, SettingsWriter {

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
    public void setServerName(String serverName) {
        sharedPreferences.edit()
                .putString(SERVER_NAME_KEY, serverName)
                .apply();
    }

    @Override
    public void setCaPort(int caPort) {
        sharedPreferences.edit()
                .putInt(CA_PORT_KEY, caPort)
                .apply();
    }

    @Override
    public void setRegistrationPort(int registrationPort) {
        sharedPreferences.edit()
                .putInt(REGISTRATION_PORT_KEY, registrationPort)
                .apply();
    }

    @Override
    public void setServerPort(int serverPort) {
        sharedPreferences.edit()
                .putInt(SERVER_PORT_KEY, serverPort)
                .apply();
    }

    @Override
    public void setUserId(int userId) {
        sharedPreferences.edit()
                .putInt(USER_ID_KEY, userId)
                .apply();
    }

    @Override
    public void setUserName(String userName) {
        sharedPreferences.edit()
                .putString(USER_NAME_KEY, userName)
                .apply();
    }

    @Override
    public void setUserDeviceId(int userDeviceId) {
        sharedPreferences.edit()
                .putInt(USER_DEVICE_ID_KEY, userDeviceId)
                .apply();
    }

    @Override
    public void setUserDeviceName(String userDeviceName) {
        sharedPreferences.edit()
                .putString(USER_DEVICE_NAME_KEY, userDeviceName)
                .apply();
    }

    @Override
    public void setFingerprint(String fingerprint) {
        sharedPreferences.edit()
                .putString(FINGERPRINT_KEY, fingerprint)
                .apply();
    }

    @Override
    public void setTicket(String ticket) {
        sharedPreferences.edit()
                .putString(TICKET_KEY, ticket)
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
}
