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

package de.njsm.stocks.clientold.network;

import de.njsm.stocks.clientold.config.Configuration;
import de.njsm.stocks.clientold.exceptions.CryptoException;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;


public class HttpClientFactory {

    private static final Logger LOG = LogManager.getLogger(HttpClientFactory.class);

    public static OkHttpClient getClient() throws CryptoException {

        try {
            LOG.info("Getting new http client");
            LOG.info("Keystore is " + Configuration.KEYSTORE_PATH);

            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(new FileInputStream(Configuration.KEYSTORE_PATH),
                    Configuration.KEYSTORE_PASSWORD.toCharArray());

            return getClient(ks);
        } catch (Exception e) {
            throw error(e);
        }
    }

    public static OkHttpClient getClient(KeyStore ks) throws CryptoException {

        try {
            LOG.info("Getting new http client");

            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, Configuration.KEYSTORE_PASSWORD.toCharArray());

            SSLContext context = SSLContext.getInstance("TLSv1.2");
            context.init(kmf.getKeyManagers(),
                    tmf.getTrustManagers(),
                    new SecureRandom());

            return new OkHttpClient.Builder()
                    .sslSocketFactory(context.getSocketFactory(), (X509TrustManager) tmf.getTrustManagers()[0])
                    .hostnameVerifier((s, sslSession) -> true)
                    .build();
        } catch (Exception e) {
            throw error(e);
        }

    }

    private static CryptoException error(Exception e) throws CryptoException {
        throw new CryptoException("There is a problem with the key store", e);
    }
}
