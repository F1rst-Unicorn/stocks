package de.njsm.stocks.client.network;

import com.squareup.okhttp.OkHttpClient;
import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.CryptoException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;


public class HttpClientFactory {

    public static OkHttpClient getClient() throws CryptoException {

        try {
            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(new FileInputStream(Configuration.KEYSTORE_PATH),
                    Configuration.KEYSTORE_PASSWORD.toCharArray());
            tmf.init(ks);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, Configuration.KEYSTORE_PASSWORD.toCharArray());

            SSLContext context = SSLContext.getInstance("TLSv1.2");
            context.init(kmf.getKeyManagers(),
                    tmf.getTrustManagers(),
                    new SecureRandom());

            return new OkHttpClient()
                    .setSslSocketFactory(context.getSocketFactory())
                    .setHostnameVerifier((s, sslSession) -> true);
        } catch (Exception e) {
            throw new CryptoException("There is a problem with the key store", e);
        }

    }
}
