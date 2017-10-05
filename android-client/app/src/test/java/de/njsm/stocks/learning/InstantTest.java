package de.njsm.stocks.learning;

import org.junit.Test;
import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

public class InstantTest {

    @Test
    public void parseStringToInstantWithoutTimezone() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.of("UTC"));

        String timestamp = "1970-01-01 00:00:00.000";
        Instant temporalAccessor = Instant.from(formatter.parse(timestamp));
    }
}
