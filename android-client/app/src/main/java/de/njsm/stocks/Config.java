package de.njsm.stocks;

import okhttp3.OkHttpClient;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;

public class Config {

    public static final String PREFERENCES_FILE = "stocks_prefs";
    public static final String KEYSTORE_FILE = "keystore";

    public static final String LOG_TAG = "de.njsm.stocks";

    public static final String SERVER_NAME_CONFIG = "stocks.serverName";
    public static final String CA_PORT_CONFIG = "stocks.caPort";
    public static final String SENTRY_PORT_CONFIG = "stocks.sentryPort";
    public static final String SERVER_PORT_CONFIG = "stocks.serverPort";
    public static final String USERNAME_CONFIG = "stocks.username";
    public static final String DEVICE_NAME_CONFIG = "stocks.deviceName";
    public static final String UID_CONFIG = "stocks.uid";
    public static final String DID_CONFIG = "stocks.did";
    public static final String FPR_CONFIG = "stocks.fpr";
    public static final String TICKET_CONFIG = "stocks.ticket";

    public static final String PASSWORD = "passwordfooyouneverguessme$32XD";

    public static final SimpleDateFormat TECHNICAL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss.S");

    public static OkHttpClient getClient(InputStream keystoreStream) throws Exception {

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(keystoreStream, PASSWORD.toCharArray());
        tmf.init(ks);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
        kmf.init(ks, PASSWORD.toCharArray());

        SSLContext context = SSLContext.getInstance("TLSv1.2");
        context.init(kmf.getKeyManagers(),
                tmf.getTrustManagers(),
                new SecureRandom());

        return new OkHttpClient.Builder().
                sslSocketFactory(context.getSocketFactory())
                .hostnameVerifier(((hostname, session) -> true))
                .build();
    }

}
