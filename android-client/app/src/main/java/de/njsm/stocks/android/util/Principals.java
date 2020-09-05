/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.android.util;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Stores the principals of a user during ticket
 * creation
 */
public class Principals {

    private String username;
    private String deviceName;
    private int uid;
    private int did;

    public Principals(String[] rawInput) {

        if (rawInput.length != 4) {
            throw new SecurityException("client name malformed");
        }

        username = rawInput[0];
        deviceName = rawInput[2];
        try {
            uid = Integer.parseInt(rawInput[1]);
            did = Integer.parseInt(rawInput[3]);
        } catch (NumberFormatException e) {
            throw new SecurityException("client IDs are invalid");
        }
    }

    public Principals(String username, String deviceName, int uid, int did) {
        this.username = username;
        this.deviceName = deviceName;
        this.uid = uid;
        this.did = did;
    }

    public Principals(String username, String deviceName, String uid, String did) {
        this(new String[] {username, uid, deviceName, did});
    }

    public String getUsername() {
        return username;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public int getUid() {
        return uid;
    }

    public int getDid() {
        return did;
    }

    public String getReadableString() {
        return username + "@" + deviceName;
    }

    @Override
    @NonNull
    public String toString() {
        return username + "$" + uid + "$" + deviceName + "$" + did;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Principals that = (Principals) o;
        return uid == that.uid &&
                did == that.did &&
                Objects.equals(username, that.username) &&
                Objects.equals(deviceName, that.deviceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, deviceName, uid, did);
    }

    public static boolean isNameValid(String name) {
        int noDollar = name.indexOf('$');
        int noEqual  = name.indexOf('=');
        return noDollar == -1 && noEqual == -1;
    }
}
