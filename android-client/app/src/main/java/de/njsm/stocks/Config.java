package de.njsm.stocks;

import android.content.Context;
import android.content.SharedPreferences;

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

    protected String serverName;
    protected int caPort;
    protected int sentryPort;
    protected int serverPort;
    protected String username;
    protected String deviceName;
    protected int uid;
    protected int did;
    protected String fpr;
    protected String ticket;

    protected final String password = "passwordfooyouneverguessme$32XD";
    protected Context c;
    protected SharedPreferences prefs;

    public Config(Context c) {
        this.c = c;
        prefs = c.getSharedPreferences(preferences, Context.MODE_PRIVATE);
        refresh();
    }

    public OkHttpClient getClient() throws Exception {

        TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(c.openFileInput("keystore"),
                getPassword().toCharArray());
        tmf.init(ks);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, getPassword().toCharArray());

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


    public boolean isConfigured() {
        return prefs.contains(serverNameConfig);
    }

    public void refresh() {
        serverName = prefs.getString(serverNameConfig, "");
        caPort = prefs.getInt(caPortConfig, 10910);
        sentryPort = prefs.getInt(sentryPortConfig, 10911);
        serverPort = prefs.getInt(serverPortConfig, 10912);
        username = prefs.getString(usernameConfig, "");
        deviceName = prefs.getString(deviceNameConfig, "");
        uid = prefs.getInt(uidConfig, 0);
        did = prefs.getInt(didConfig, 0);
        fpr = prefs.getString(fprConfig, "");
        ticket = prefs.getString(ticketConfig, "");
    }

    public SharedPreferences getPrefs() {
        return prefs;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getCaPort() {
        return caPort;
    }

    public void setCaPort(int caPort) {
        this.caPort = caPort;
    }

    public int getSentryPort() {
        return sentryPort;
    }

    public void setSentryPort(int sentryPort) {
        this.sentryPort = sentryPort;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.sentryPort = serverPort;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getDid() {
        return did;
    }

    public void setDid(int did) {
        this.did = did;
    }

    public String getFpr() {
        return fpr;
    }

    public void setFpr(String fpr) {
        this.fpr = fpr;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getPassword() {
        return password;
    }
}
