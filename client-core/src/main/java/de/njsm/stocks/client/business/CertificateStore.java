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

import de.njsm.stocks.client.business.entities.PemFile;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.util.List;

public interface CertificateStore {

    void storeCertificates(List<PemFile> certificates) throws SubsystemException;

    /**
     * @param certificates Put the CA certificate first
     */
    void storeKey(KeyPair keyPair, List<PemFile> certificates) throws SubsystemException;

    KeyStore getKeystore() throws SubsystemException;

    KeyManagerFactory getKeyManager() throws SubsystemException;

    TrustManagerFactory getTrustManager() throws SubsystemException;

    String getCaCertificateFingerprint() throws SubsystemException;

    void clear();
}
