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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AutoValue
public abstract class RegistrationForm {

    private static final Logger LOG = LoggerFactory.getLogger(RegistrationForm.class);

    public static RegistrationForm.Builder builder() {
        return new AutoValue_RegistrationForm.Builder();
    }

    public static RegistrationForm empty() {
        return RegistrationForm.builder()
                .serverName("")
                .caPort(0)
                .registrationPort(0)
                .serverPort(0)
                .userName("")
                .userId(0)
                .userDeviceName("")
                .userDeviceId(0)
                .fingerprint("")
                .ticket("")
                .build();
    }

    public static RegistrationForm parseRawString(String input) {
        String[] arguments = input.split("\n");
        if (arguments.length != 10)
            return empty();

        return RegistrationForm.builder()
                .serverName(arguments[6])
                .caPort(parseIntSafely(arguments[7]))
                .registrationPort(parseIntSafely(arguments[8]))
                .serverPort(parseIntSafely(arguments[9]))
                .userName(arguments[0])
                .userId(parseIntSafely(arguments[2]))
                .userDeviceName(arguments[1])
                .userDeviceId(parseIntSafely(arguments[3]))
                .fingerprint(arguments[4])
                .ticket(arguments[5])
                .build();
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

    public Principals toPrincipals() {
        return Principals.builder()
                .userName(userName())
                .userId(userId())
                .userDeviceName(userDeviceName())
                .userDeviceId(userDeviceId())
                .build();
    }

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

    private static int parseIntSafely(String rawInt) {
        try {
            return Integer.parseInt(rawInt);
        } catch (NumberFormatException e) {
            LOG.warn("invalid number '" + rawInt + "'");
            return 0;
        }
    }

    public String toQrString() {
        return String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n",
                userName(),
                userDeviceName(),
                userId(),
                userDeviceId(),
                fingerprint(),
                ticket(),
                serverName(),
                caPort(),
                registrationPort(),
                serverPort()
        );
    }
}
