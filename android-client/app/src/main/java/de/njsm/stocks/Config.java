package de.njsm.stocks;

import android.content.Context;

import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.OkHttpClient;

public class Config {

    public static final String preferences = "stocks_prefs";
    public static final String log = "de.njsm.stocks";

    public static final String serverNameConfig = "stocks.serverName";
    public static final String caPortConfig = "stocks.caPort";
    public static final String sentryPortConfig = "stocks.sentryPort";
    public static final String serverPortConfig = "stocks.serverPort";
    public static final String usernameConfig = "stocks.username";
    public static final String deviceNameConfig = "stocks.deviceName";
    public static final String uidConfig = "stocks.uid";
    public static final String didConfig = "stocks.did";
    public static final String fprConfig = "stocks.fpr";
    public static final String ticketConfig = "stocks.ticket";

    public static final String password = "passwordfooyouneverguessme$32XD";

    public static OkHttpClient getClient(Context c) throws Exception {

        TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(c.openFileInput("keystore"),
                password.toCharArray());
        tmf.init(ks);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
        kmf.init(ks, password.toCharArray());

        SSLContext context = SSLContext.getInstance("TLSv1.2");
        context.init(kmf.getKeyManagers(),
                tmf.getTrustManagers(),
                new SecureRandom());

        return new OkHttpClient.Builder().
                sslSocketFactory(context.getSocketFactory())
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();
    }

}
