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

package de.njsm.stocks.client.crypto;

import de.njsm.stocks.client.business.entities.KeyGenerationParameters;
import de.njsm.stocks.client.business.entities.Principals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class KeyGeneratorImplTest {

    private KeyGeneratorImpl uut;

    private KeyGenerationParameters parameters;

    @BeforeEach
    void setUp() {
        parameters = KeyGenerationParameters.builder()
                .keySize(512)
                .keyAlgorithm("RSA")
                .signingAlgorithm("SHA256WithRSA")
                .build();

        uut = new KeyGeneratorImpl();
    }

    @Test
    void keyGenerationWorks() {
        uut.generateKeyPair(parameters);
    }

    @Test
    void csrGenerationWorks() {
        Principals principals = Principals.builder()
                .userName("Jack")
                .userId(1)
                .userDeviceName("Mobile")
                .userDeviceId(2)
                .build();

        String actual = uut.generateCertificateSigningRequest(uut.generateKeyPair(parameters), principals, parameters);

        assertTrue(actual.startsWith("-----BEGIN CERTIFICATE REQUEST-----\n"));
        assertTrue(actual.endsWith("-----END CERTIFICATE REQUEST-----\n"));
    }
}
