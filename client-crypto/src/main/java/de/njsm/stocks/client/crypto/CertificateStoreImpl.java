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

import de.njsm.stocks.client.business.CertificateStore;
import de.njsm.stocks.client.business.SubsystemException;
import de.njsm.stocks.client.business.entities.PemFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;

class CertificateStoreImpl implements CertificateStore {

    private static final Logger LOG = LoggerFactory.getLogger(CertificateStoreImpl.class);

    private static final String PASSWORD = "passwordfooyouneverguessme$32XD";

    private static final String KEYSTORE_FILE = "keystore";

    private final FileInteractor fileInteractor;

    private final File keystoreFile;

    @Inject
    public CertificateStoreImpl(FileInteractor fileInteractor) {
        this.fileInteractor = fileInteractor;
        keystoreFile = new File(KEYSTORE_FILE);
    }

    @Override
    public void storeCertificates(List<PemFile> certificates) throws SubsystemException {
        try {
            storeCertificatesInternally(certificates);
        } catch (CertificateException | KeyStoreException | IOException | NoSuchAlgorithmException e) {
            throw new CryptoException("storing certificate failed", e);
        }
    }

    @Override
    public void storeKey(KeyPair keyPair, List<PemFile> certificates) throws SubsystemException {
        try {
            storeKeyInternally(keyPair, certificates);
        } catch (CertificateException | KeyStoreException | IOException | NoSuchAlgorithmException e) {
            throw new CryptoException("storing key", e);
        }
    }

    @Override
    public KeyStore getKeystore() throws SubsystemException {
        try {
            return getKeystoreInternally();
        } catch (CertificateException | KeyStoreException | IOException | NoSuchAlgorithmException e) {
            throw new CryptoException("getting keystore", e);
        }
    }

    @Override
    public KeyManagerFactory getKeyManager() throws SubsystemException {
        try {
            KeyStore keyStore = getKeystoreInternally();
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, PASSWORD.toCharArray());
            return kmf;
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException | IOException e) {
            throw new CryptoException(e);
        }
    }

    @Override
    public TrustManagerFactory getTrustManager() throws SubsystemException {
        try {
            KeyStore keyStore = getKeystoreInternally();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            return tmf;
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            throw new CryptoException(e);
        }
    }

    @Override
    public String getCaCertificateFingerprint() throws SubsystemException {
        try {
            KeyStore keyStore = getKeystoreInternally();
            Certificate certificate = keyStore.getCertificate("ca");

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(certificate.getEncoded());
            byte[] digest = md.digest();

            char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
            List<String> bytes = new ArrayList<>();
            for (byte aDigest : digest) {
                String word = String.valueOf(hexDigits[(aDigest & 0xf0) >> 4]);
                word += hexDigits[aDigest & 0x0f];
                bytes.add(word);
            }

            return String.join(":", bytes);
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            throw new CryptoException(e);
        }
    }

    @Override
    public void clear() {
        keystoreFile.delete();
    }

    private void storeKeyInternally(KeyPair keyPair, List<PemFile> certificates) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        KeyStore keyStore = getKeystoreInternally();
        Certificate[] trustChain = buildTrustChain(certificates);
        keyStore.setKeyEntry("client", keyPair.getPrivate(), PASSWORD.toCharArray(), trustChain);
        store(keyStore);
    }

    private Certificate[] buildTrustChain(List<PemFile> certificates) throws CertificateException {
        Certificate[] trustChain = new Certificate[certificates.size()];
        int i = trustChain.length - 1;
        for (PemFile certificate : certificates) {
            Certificate parsed = convertToCertificate(certificate.pemCertificate());
            trustChain[i] = parsed;
            i--;
        }
        return trustChain;
    }

    private void storeCertificatesInternally(List<PemFile> certificates) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        KeyStore keyStore = getKeystoreInternally();
        for (PemFile certificate : certificates) {
            Certificate parsed = convertToCertificate(certificate.pemCertificate());
            keyStore.setCertificateEntry(certificate.name(), parsed);
        }
        store(keyStore);
    }

    private Certificate convertToCertificate(String pemString) throws CertificateException {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        InputStream stringStream = new ByteArrayInputStream(pemString.getBytes(StandardCharsets.UTF_8));
        return factory.generateCertificate(stringStream);
    }

    private KeyStore getKeystoreInternally() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        if (fileInteractor.doesFileExist(keystoreFile)) {
            return loadExistingKeystore();
        } else {
            return createNewKeystore();
        }
    }

    private void store(KeyStore keystore) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        OutputStream out = fileInteractor.getFileOutputStream(keystoreFile);
        keystore.store(out, PASSWORD.toCharArray());
        out.flush();
        out.close();
    }

    private KeyStore loadExistingKeystore() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        LOG.debug("loading existing keystore");
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(fileInteractor.getFileInputStream(keystoreFile), PASSWORD.toCharArray());
        return keystore;
    }

    private KeyStore createNewKeystore() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        LOG.debug("creating new keystore");
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(null);
        return keystore;
    }
}
