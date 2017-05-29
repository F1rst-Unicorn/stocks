package de.njsm.stocks.client;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utils {

    public static final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public static Date getDate(String dateString) throws ParseException {
        return format.parse(dateString);
    }

    public static int getTimezoneOffset() {
        return TimeZone.getDefault().getOffset(System.currentTimeMillis());
    }

}
