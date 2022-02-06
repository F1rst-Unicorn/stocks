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

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import de.njsm.stocks.client.business.entities.PemFile;
import de.njsm.stocks.client.business.entities.RegistrationCsr;
import de.njsm.stocks.client.business.entities.RegistrationForm;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

class SetupInteractorImpl implements SetupInteractor {

    private static final Logger LOG = LoggerFactory.getLogger(SetupInteractorImpl.class);

    private final SettingsWriter settingsWriter;

    private final CertificateFetcherBuilder certificateFetcherBuilder;

    private final RegistratorBuilder registratorBuilder;

    private final CertificateStore certificateStore;

    private final KeyPairGenerator keyPairGenerator;

    private final List<PemFile> certificates;

    @Inject
    public SetupInteractorImpl(SettingsWriter settingsWriter, CertificateFetcherBuilder certificateFetcherBuilder, RegistratorBuilder registratorBuilder, CertificateStore certificateStore, KeyPairGenerator keyPairGenerator) {
        this.settingsWriter = settingsWriter;
        this.certificateFetcherBuilder = certificateFetcherBuilder;
        this.registratorBuilder = registratorBuilder;
        this.certificateStore = certificateStore;
        this.keyPairGenerator = keyPairGenerator;

        certificates = new ArrayList<>();
    }

    public void generateKeys() {
        keyPairGenerator.generate();
    }

    public void setup(RegistrationForm form) {
        downloadCa(form);
        verifyCa(form);
        register(form);
        storeSettings(form);
    }

    private void downloadCa(RegistrationForm form) {
        CertificateFetcher fetcher = certificateFetcherBuilder.build(form.serverName(), form.caPort());

        certificates.add(PemFile.create("ca", fetcher.getCaCertificate()));
        certificates.add(PemFile.create("intermediate", fetcher.getIntermediateCertificate()));

        certificateStore.storeCertificates(certificates);
    }

    private void verifyCa(RegistrationForm form) {
        String actualFpr = certificateStore.getCaCertificateFingerprint();
        String expectedFpr = form.fingerprint();

        if (!expectedFpr.equals(actualFpr)) {
            LOG.error("'" + expectedFpr + "'");
            LOG.error("'" + actualFpr + "'");
            throw new RuntimeException("fingerprints dont match");
        }
        throw new RuntimeException("clean up in error case!");
    }

    private void register(RegistrationForm form) {
        Registrator registrator = registratorBuilder.build(form.serverName(), form.registrationPort(), certificateStore.getTrustManager(), certificateStore.getKeyManager());
        RegistrationCsr csr = RegistrationCsr.create(form.userDeviceId(), form.ticket(), keyPairGenerator.getCsr());
        PemFile clientCertificate = PemFile.create("client", registrator.getOwnCertificate(csr));
        certificates.add(clientCertificate);
        certificateStore.storeKey(keyPairGenerator.getKeyPair(), certificates);
    }

    private void storeSettings(RegistrationForm form) {
        settingsWriter.store(form);
    }

}
