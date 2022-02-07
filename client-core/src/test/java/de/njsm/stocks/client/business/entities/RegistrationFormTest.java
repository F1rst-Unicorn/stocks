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
 */

package de.njsm.stocks.client.business.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationFormTest {

    private RegistrationForm uut;

    @Test
    void transformingToPrincipalsWorks() {
        uut = RegistrationForm.builder()
                .serverName("serverName")
                .caPort(10910)
                .registrationPort(10911)
                .serverPort(10912)
                .userName("userName")
                .userId(1)
                .userDeviceName("userDeviceName")
                .userDeviceId(2)
                .fingerprint("fingerprint")
                .ticket("ticket")
                .build();

        Principals actual = uut.toPrincipals();

        assertEquals(uut.userName(), actual.userName());
        assertEquals(uut.userId(), actual.userId());
        assertEquals(uut.userDeviceName(), actual.userDeviceName());
        assertEquals(uut.userDeviceId(), actual.userDeviceId());
    }
}
