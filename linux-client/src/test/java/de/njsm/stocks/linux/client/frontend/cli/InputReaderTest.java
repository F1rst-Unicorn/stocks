package de.njsm.stocks.linux.client.frontend.cli;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.util.Date;

public class InputReaderTest {

    @Test
    public void testDateParsing() {
        String validDate = "31-12-2017\n";
        InputReader uut = new InputReader(IOUtils.toInputStream(validDate));

        Date date = uut.nextDate();
        date.toString();

    }

}
