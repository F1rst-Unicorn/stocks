package de.njsm.stocks.client.network;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.CryptoException;
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
