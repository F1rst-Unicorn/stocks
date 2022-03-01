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
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

class SetupInteractorImpl implements SetupInteractor {

    private static final Logger LOG = LoggerFactory.getLogger(SetupInteractorImpl.class);

    private final SettingsWriter settingsWriter;

    private final CertificateFetcher certificateFetcher;

    private final Registrator registrator;

    private final CertificateStore certificateStore;

    private final KeyGenerator keyPairGenerator;

    private final List<PemFile> certificates;

    private final BehaviorSubject<SetupState> currentState;

    private KeyPair keyPair;

    private final ArrayBlockingQueue<RegistrationForm> queue;

    @Inject
    public SetupInteractorImpl(SettingsWriter settingsWriter,
                               CertificateFetcher certificateFetcher,
                               Registrator registrator,
                               CertificateStore certificateStore,
                               KeyGenerator keyPairGenerator) {
        this.settingsWriter = settingsWriter;
        this.certificateFetcher = certificateFetcher;
        this.registrator = registrator;
        this.certificateStore = certificateStore;
        this.keyPairGenerator = keyPairGenerator;

        certificates = new ArrayList<>();
        currentState = BehaviorSubject.create();
        queue = new ArrayBlockingQueue<>(2);
    }

    @Override
    public Observable<SetupState> setupWithForm(RegistrationForm registrationForm) {
        queue.add(registrationForm);
        return currentState;
    }

    public void setup() {
        generateKeys();

        while (true) {
            try {
                RegistrationForm form = queue.take();
                cleanState();
                downloadCa(form);
                verifyCa(form);
                register(form);
                storeSettings(form);
                publishNewState(SetupState.SUCCESS);
                break;
            } catch (SetupStateException e) {
                // continue
            } catch (InterruptedException e) {
                LOG.error("Interrupted while waiting for registration form", e);
                LOG.error("Giving up setup");
                break;
            }
        }
    }

    private void generateKeys() {
        doFailingSetupStep(SetupState.GENERATING_KEYS, SetupState.GENERATING_KEYS_FAILED, "key generation failed", () ->
            keyPair = keyPairGenerator.generateKeyPair(KeyGenerationParameters.secureDefault())
        );
    }

    private void cleanState() {
        certificateStore.clear();
        settingsWriter.store(RegistrationForm.empty());
    }

    private void downloadCa(RegistrationForm form) {
        doFailingSetupStep(SetupState.FETCHING_CERTIFICATE, SetupState.FETCHING_CERTIFICATE_FAILED, "fetching ceritifcate failed", () -> {

            certificates.add(PemFile.create("ca", certificateFetcher.getCaCertificate(form.certificateEndpoint())));
            certificates.add(PemFile.create("intermediate", certificateFetcher.getIntermediateCertificate(form.certificateEndpoint())));

            certificateStore.storeCertificates(certificates);
        });
    }

    private void verifyCa(RegistrationForm form) {
        doFailingSetupStep(SetupState.VERIFYING_CERTIFICATE, SetupState.VERIFYING_CERTIFICATE_FAILED, "Verifying CA failed", () -> {
            String actualFpr = certificateStore.getCaCertificateFingerprint();
            String expectedFpr = form.fingerprint();

            if (!expectedFpr.equals(actualFpr)) {
                LOG.error("'" + expectedFpr + "'");
                LOG.error("'" + actualFpr + "'");
                throw new SubsystemException();
            }
        });
    }

    private void register(RegistrationForm form) {
        doFailingSetupStep(SetupState.REGISTERING_KEY, SetupState.REGISTERING_KEY_FAILED, "Registration failed", () -> {
            String csrPemContent = keyPairGenerator.generateCertificateSigningRequest(keyPair, form.toPrincipals(), KeyGenerationParameters.secureDefault());
            RegistrationCsr csr = RegistrationCsr.create(form.userDeviceId(), form.ticket(), csrPemContent);
            RegistrationEndpoint registrationEndpoint = form.registrationEndpoint(certificateStore.getTrustManager(), certificateStore.getKeyManager());
            PemFile clientCertificate = PemFile.create("client", registrator.getOwnCertificate(registrationEndpoint, csr));
            certificates.add(clientCertificate);
            certificateStore.storeKey(keyPair, certificates);
        });
    }

    private void storeSettings(RegistrationForm form) {
        publishNewState(SetupState.STORING_SETTINGS);
        settingsWriter.store(form);
    }

    private void doFailingSetupStep(SetupState progressingState, SetupState failureState, String failureLogMessage, Runnable step) {
        publishNewState(progressingState);
        try {
            step.run();
        } catch (SubsystemException e) {
            LOG.warn(failureLogMessage, e);
            publishNewState(failureState);
            throw new SetupStateException(failureState);
        }
    }


    private void publishNewState(SetupState newState) {
        currentState.onNext(newState);
    }

    private static final class SetupStateException extends RuntimeException {
        private final SetupState state;

        public SetupStateException(SetupState state) {
            this.state = state;
        }

        public SetupState getState() {
            return state;
        }
    }
}
