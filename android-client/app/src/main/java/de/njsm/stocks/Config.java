package de.njsm.stocks;

import android.content.Context;
import android.content.SharedPreferences;

public class Config {

    public static final String preferences = "stocks_prefs";

    public static final String serverName = "stocks.serverName";
    public static final String caPort = "stocks.caPort";
    public static final String sentryPort = "stocks.sentryPort";
    public static final String serverPort = "stocks.serverPort";
    public static final String username = "stocks.username";
    public static final String deviceName = "stocks.deviceName";
    public static final String uid = "stocks.uid";
    public static final String did = "stocks.did";
    public static final String fpr = "stocks.fpr";
    public static final String ticket = "stocks.ticket";



    protected SharedPreferences prefs;

    public Config(Context c) {
        prefs = c.getSharedPreferences(preferences, Context.MODE_PRIVATE);

    }

    public boolean isConfigured() {
        return prefs.contains(serverName);
    }

    public SharedPreferences getPrefs() {
        return prefs;
    }



}
