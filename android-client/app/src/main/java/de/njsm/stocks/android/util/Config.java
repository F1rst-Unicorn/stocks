/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.android.util;

import android.content.SharedPreferences;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Locale;

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
    public static final DateTimeFormatter PRETTY_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yy").withZone(ZoneId.systemDefault());

    public static String formatServerUrl(SharedPreferences prefs) {
        return formatServerUrl(prefs.getString(Config.SERVER_NAME_CONFIG, ""),
                prefs.getInt(Config.SERVER_PORT_CONFIG, 0));
    }

    public static String formatServerUrl(String hostname, int port) {
        return String.format(Locale.US, "https://%s:%d/",
                hostname,
                port);
    }
}
