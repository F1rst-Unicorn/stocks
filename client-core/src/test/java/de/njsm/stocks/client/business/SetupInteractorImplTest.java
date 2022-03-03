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

import de.njsm.stocks.client.business.entities.*;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyPair;

import static de.njsm.stocks.client.business.entities.Entities.registrationForm;
import static de.njsm.stocks.client.business.entities.KeyGenerationParameters.secureDefault;
import static java.util.Arrays.asList;
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
        verifyNoMoreInteractions(settingsWriter);
        verifyNoMoreInteractions(certificateFetcher);
        verifyNoMoreInteractions(registrator);
        verifyNoMoreInteractions(certificateStore);
        verifyNoMoreInteractions(keyGenerator);
    }

    @Test
    void failingKeyGenerationIsReported() {
        when(keyGenerator.generateKeyPair(secureDefault())).thenThrow(new SubsystemException("test"));

        assertThrows(RuntimeException.class, () -> uut.setup());

        verify(keyGenerator).generateKeyPair(secureDefault());
        uut.setupWithForm(registrationForm()).test().assertValue(SetupState.GENERATING_KEYS_FAILED);
    }

    @Test
    void interruptedQueueWaitingGivesUpSetup() {
        RegistrationForm form = registrationForm();
        when(keyGenerator.generateKeyPair(secureDefault())).thenReturn(new KeyPair(null, null));
        Observable<SetupState> result = uut.setupWithForm(form);
        Thread.currentThread().interrupt();

        uut.setup();

        verify(keyGenerator).generateKeyPair(secureDefault());
        result.test().assertValue(SetupState.FETCHING_CERTIFICATE_FAILED);
    }

    @Test
    void failingCaCertificateFetchingIsReported() {
        RegistrationForm form = registrationForm();
        when(keyGenerator.generateKeyPair(secureDefault())).thenReturn(new KeyPair(null, null));
        when(certificateFetcher.getCaCertificate(form.certificateEndpoint())).thenThrow(new SubsystemException("test"));
        Observable<SetupState> result = uut.setupWithForm(form);
        uut.giveUpRetrying();

        uut.setup();

        verify(keyGenerator).generateKeyPair(secureDefault());
        verify(certificateFetcher).getCaCertificate(form.certificateEndpoint());
        verify(settingsWriter).store(RegistrationForm.empty());
        verify(certificateStore).clear();
        result.test().assertValue(SetupState.FETCHING_CERTIFICATE_FAILED);
    }

    @Test
    void failingChainCertificateFetchingIsReported() {
        RegistrationForm form = registrationForm();
        when(keyGenerator.generateKeyPair(secureDefault())).thenReturn(new KeyPair(null, null));
        when(certificateFetcher.getCaCertificate(form.certificateEndpoint())).thenReturn("ca");
        when(certificateFetcher.getIntermediateCertificate(form.certificateEndpoint())).thenThrow(new SubsystemException("test"));
        Observable<SetupState> result = uut.setupWithForm(form);
        uut.giveUpRetrying();

        uut.setup();

        verify(keyGenerator).generateKeyPair(secureDefault());
        verify(settingsWriter).store(RegistrationForm.empty());
        verify(certificateStore).clear();
        verify(certificateFetcher).getCaCertificate(form.certificateEndpoint());
        verify(certificateFetcher).getIntermediateCertificate(form.certificateEndpoint());
        result.test().assertValue(SetupState.FETCHING_CERTIFICATE_FAILED);
    }

    @Test
    void failingFingerprintValidationIsReported() {
        RegistrationForm form = registrationForm();
        PemFile ca = PemFile.create("ca", "ca");
        PemFile intermediate = PemFile.create("intermediate", "intermediate");
        when(keyGenerator.generateKeyPair(secureDefault())).thenReturn(new KeyPair(null, null));
        when(certificateFetcher.getCaCertificate(form.certificateEndpoint())).thenReturn(ca.pemCertificate());
        when(certificateFetcher.getIntermediateCertificate(form.certificateEndpoint())).thenReturn(intermediate.pemCertificate());
        when(certificateStore.getCaCertificateFingerprint()).thenReturn(form.fingerprint() + " different");
        Observable<SetupState> result = uut.setupWithForm(form);
        uut.giveUpRetrying();

        uut.setup();

        verify(keyGenerator).generateKeyPair(secureDefault());
        verify(settingsWriter).store(RegistrationForm.empty());
        verify(certificateStore).clear();
        verify(certificateFetcher).getCaCertificate(form.certificateEndpoint());
        verify(certificateFetcher).getIntermediateCertificate(form.certificateEndpoint());
        verify(certificateStore).storeCertificates(asList(ca, intermediate));
        result.test().assertValue(SetupState.VERIFYING_CERTIFICATE_FAILED);
    }

    @Test
    void failingRegistrationIsReported() {
        RegistrationForm form = registrationForm();
        KeyPair keyPair = new KeyPair(null, null);
        PemFile ca = PemFile.create("ca", "ca");
        PemFile intermediate = PemFile.create("intermediate", "intermediate");
        when(keyGenerator.generateKeyPair(secureDefault())).thenReturn(keyPair);
        when(certificateFetcher.getCaCertificate(form.certificateEndpoint())).thenReturn(ca.pemCertificate());
        when(certificateFetcher.getIntermediateCertificate(form.certificateEndpoint())).thenReturn(intermediate.pemCertificate());
        when(certificateStore.getCaCertificateFingerprint()).thenReturn(form.fingerprint());
        when(keyGenerator.generateCertificateSigningRequest(keyPair, form.toPrincipals(), secureDefault())).thenReturn("csr");
        when(certificateStore.getTrustManager()).thenReturn(mock(TrustManagerFactory.class));
        when(certificateStore.getKeyManager()).thenReturn(mock(KeyManagerFactory.class));
        when(registrator.getOwnCertificate(any(RegistrationEndpoint.class), any(RegistrationCsr.class))).thenThrow(new SubsystemException("test"));
        Observable<SetupState> result = uut.setupWithForm(form);
        uut.giveUpRetrying();

        uut.setup();

        verify(keyGenerator).generateKeyPair(secureDefault());
        verify(settingsWriter).store(RegistrationForm.empty());
        verify(certificateStore).clear();
        verify(certificateFetcher).getCaCertificate(form.certificateEndpoint());
        verify(certificateFetcher).getIntermediateCertificate(form.certificateEndpoint());
        verify(certificateStore).storeCertificates(asList(ca, intermediate));
        verify(certificateStore).getTrustManager();
        verify(certificateStore).getKeyManager();
        verify(registrator).getOwnCertificate(any(RegistrationEndpoint.class), any(RegistrationCsr.class));
        result.test().assertValue(SetupState.REGISTERING_KEY_FAILED);
    }

    @Test
    void successfulRegistrationWorks() {
        RegistrationForm form = registrationForm();
        KeyPair keyPair = new KeyPair(null, null);
        PemFile ca = PemFile.create("ca", "ca");
        PemFile intermediate = PemFile.create("intermediate", "intermediate");
        PemFile clientCertificate = PemFile.create("client", "certificate");
        when(keyGenerator.generateKeyPair(secureDefault())).thenReturn(keyPair);
        when(certificateFetcher.getCaCertificate(form.certificateEndpoint())).thenReturn(ca.pemCertificate());
        when(certificateFetcher.getIntermediateCertificate(form.certificateEndpoint())).thenReturn(intermediate.pemCertificate());
        when(certificateStore.getCaCertificateFingerprint()).thenReturn(form.fingerprint());
        when(keyGenerator.generateCertificateSigningRequest(keyPair, form.toPrincipals(), secureDefault())).thenReturn("csr");
        when(certificateStore.getTrustManager()).thenReturn(mock(TrustManagerFactory.class));
        when(certificateStore.getKeyManager()).thenReturn(mock(KeyManagerFactory.class));
        when(registrator.getOwnCertificate(any(RegistrationEndpoint.class), any(RegistrationCsr.class))).thenReturn(clientCertificate.pemCertificate());
        Observable<SetupState> result = uut.setupWithForm(form);

        uut.setup();

        verify(keyGenerator).generateKeyPair(secureDefault());
        verify(settingsWriter).store(RegistrationForm.empty());
        verify(certificateStore).clear();
        verify(certificateFetcher).getCaCertificate(form.certificateEndpoint());
        verify(certificateFetcher).getIntermediateCertificate(form.certificateEndpoint());
        verify(certificateStore).storeCertificates(asList(ca, intermediate));
        verify(certificateStore).getTrustManager();
        verify(certificateStore).getKeyManager();
        verify(registrator).getOwnCertificate(any(RegistrationEndpoint.class), any(RegistrationCsr.class));
        verify(certificateStore).storeKey(keyPair, asList(ca, intermediate, clientCertificate));
        verify(settingsWriter).store(form);
        result.test().assertValue(SetupState.SUCCESS);
    }

    @Test
    void failingRegistrationIsRetried() {
        RegistrationForm form = registrationForm();
        KeyPair keyPair = new KeyPair(null, null);
        PemFile ca = PemFile.create("ca", "ca");
        when(keyGenerator.generateKeyPair(secureDefault())).thenReturn(keyPair);
        when(certificateFetcher.getCaCertificate(form.certificateEndpoint())).thenThrow(new SubsystemException("test"))
                .thenReturn(ca.pemCertificate());
        when(certificateFetcher.getIntermediateCertificate(form.certificateEndpoint())).then(v -> {
            uut.giveUpRetrying();
            throw new SubsystemException("test");
        });
        Observable<SetupState> result = uut.setupWithForm(form);
        uut.setupWithForm(form);

        uut.setup();

        verify(keyGenerator).generateKeyPair(secureDefault());
        verify(settingsWriter, times(2)).store(RegistrationForm.empty());
        verify(certificateStore, times(2)).clear();
        verify(certificateFetcher, times(2)).getCaCertificate(form.certificateEndpoint());
        verify(certificateFetcher).getIntermediateCertificate(form.certificateEndpoint());
        result.test().assertValue(SetupState.FETCHING_CERTIFICATE_FAILED);
    }
}
