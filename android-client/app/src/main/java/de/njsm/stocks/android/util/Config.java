package de.njsm.stocks.android.util;

import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

public class Config {

    public static final String PREFERENCES_FILE = "stocks_prefs";
    public static final String KEYSTORE_FILE = "keystore";

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

    public static final DateTimeFormatter TECHNICAL_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.of("UTC"));
    public static final DateTimeFormatter DATABASE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd-HH:mm:ss.SSS-Z").withZone(ZoneId.of("UTC"));

}
