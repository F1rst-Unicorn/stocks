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

package de.njsm.stocks.client.business;

import de.njsm.stocks.client.business.entities.KeyGenerationParameters;
import de.njsm.stocks.client.business.entities.RegistrationForm;
import de.njsm.stocks.client.business.entities.SetupState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SetupInteractorImplTest {

    private SetupInteractorImpl uut;

    @Mock
    private SettingsWriter settingsWriter;

    @Mock
    private CertificateFetcher certificateFetcher;

    @Mock
    private Registrator registrator;

    @Mock
    private CertificateStore certificateStore;

    @Mock
    private KeyGenerator keyGenerator;

    @BeforeEach
    void setUp() {
        uut = new SetupInteractorImpl(
                settingsWriter,
                certificateFetcher,
                registrator,
                certificateStore,
                keyGenerator
        );
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(
                settingsWriter,
                certificateFetcher,
                registrator,
                certificateStore,
                keyGenerator
        );
    }

    @Test
    void failingKeyGenerationIsReported() {
        when(keyGenerator.generateKeyPair(KeyGenerationParameters.secureDefault())).thenThrow(new SubsystemException("test"));

        assertThrows(RuntimeException.class, () -> uut.setup());

        verify(keyGenerator).generateKeyPair(KeyGenerationParameters.secureDefault());
        uut.setupWithForm(RegistrationForm.empty()).test().assertValue(SetupState.GENERATING_KEYS_FAILED);
    }
}
