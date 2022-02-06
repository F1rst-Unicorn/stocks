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

package de.njsm.stocks.client.business.entities;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class RegistrationForm {

    public static RegistrationForm.Builder builder() {
        return new AutoValue_RegistrationForm.Builder();
    }

    public abstract String serverName();

    public abstract int caPort();

    public abstract int registrationPort();

    public abstract int serverPort();

    public abstract int userId();

    public abstract String userName();

    public abstract int userDeviceId();

    public abstract String userDeviceName();

    public abstract String fingerprint();

    public abstract String ticket();

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract Builder serverName(String v);

        public abstract Builder caPort(int v);

        public abstract Builder registrationPort(int v);

        public abstract Builder serverPort(int v);

        public abstract Builder userId(int v);

        public abstract Builder userName(String v);

        public abstract Builder userDeviceId(int v);

        public abstract Builder userDeviceName(String v);

        public abstract Builder fingerprint(String v);

        public abstract Builder ticket(String v);

        public abstract RegistrationForm build();
    }
}
