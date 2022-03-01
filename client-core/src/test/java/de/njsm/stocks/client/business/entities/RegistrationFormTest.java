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

import org.junit.jupiter.api.Test;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class RegistrationFormTest {

    private RegistrationForm uut;

    @Test
    void parsingFromQrCodeStringWorks() {
        RegistrationForm expected = getRegistrationForm();
        String input = expected.toQrString();

        assertEquals(expected, RegistrationForm.parseRawString(input));
    }

    @Test
    void parsingInvalidRegistrationFormReturnsEmptyOne() {
        assertEquals(RegistrationForm.empty(), RegistrationForm.parseRawString("invalid"));
    }

    @Test
    void transformingToPrincipalsWorks() {
        uut = getRegistrationForm();

        Principals actual = uut.toPrincipals();

        assertEquals(uut.userName(), actual.userName());
        assertEquals(uut.userId(), actual.userId());
        assertEquals(uut.userDeviceName(), actual.userDeviceName());
        assertEquals(uut.userDeviceId(), actual.userDeviceId());
    }

    @Test
    void gettingCertificateEndpointWorks() {
        uut = getRegistrationForm();

        CertificateEndpoint actual = uut.certificateEndpoint();

        assertEquals(uut.serverName(), actual.hostname());
        assertEquals(uut.caPort(), actual.port());
    }

    @Test
    void gettingRegistrationEndpointWorks() {
        uut = getRegistrationForm();
        TrustManagerFactory trustManagerFactory = mock(TrustManagerFactory.class);
        KeyManagerFactory keyManagerFactory = mock(KeyManagerFactory.class);

        RegistrationEndpoint actual = uut.registrationEndpoint(trustManagerFactory, keyManagerFactory);

        assertEquals(uut.serverName(), actual.hostname());
        assertEquals(uut.registrationPort(), actual.port());
        assertEquals(trustManagerFactory, actual.trustManagerFactory());
        assertEquals(keyManagerFactory, actual.keyManagerFactory());
    }

    private RegistrationForm getRegistrationForm() {
        return RegistrationForm.builder()
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
    }
}
