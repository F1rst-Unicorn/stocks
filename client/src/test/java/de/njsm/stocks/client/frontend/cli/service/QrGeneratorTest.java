package de.njsm.stocks.client.frontend.cli.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class QrGeneratorTest {

    private QrGenerator uut;

    @Before
    public void setup() throws Exception {
        uut = new QrGenerator();
    }

    @Test
    public void test() {
        String result = uut.generateQrCode("Very long name\n" +
                "Very long name\n" +
                "9999\n" +
                "9999\n" +
                "0D:A1:F9:A7:7E:02:1C:4F:34:1F:6C:89:06:77:08:80:D7:57:3D:C6:41:E8:91:9D:B5:F2:C7:54:4C:79:68:E5\n" +
                "BXYUVsLM6KQJBqRoNhKlBRh7Ah7Mv5OTnJAh2NrwZNk7G3Y9BiaWkUB3QdoUXriT");
        Assert.assertEquals(
                "████████████████████████████████████████████████████████████\n" +
                "███▀▀▀▀▀▀▀█▀▀▀███▀█▀▀▀███▀▀▀▀▀█▀█▀▀▀▀█▀▀████▀▀███▀▀▀▀▀▀▀████\n" +
                "███ █▀▀▀█ █  ▀  ▄▀  ▄▀ █ ██ ▄▀███▀  █  ▀▄ ▀ ▄▄ ██ █▀▀▀█ ████\n" +
                "███ █   █ █ ▄██ ▀ ▀  ▀  █▄▀▀ ▀  ▀█ ▀▀▀█ ▀ ▄▄  █▄█ █   █ ████\n" +
                "███ ▀▀▀▀▀ █▀▄ ▄ █▀█▀█▀▄▀▄▀▄ █▀█ █▀█ ▄ █ ▄▀█ █ █▀█ ▀▀▀▀▀ ████\n" +
                "████▀█▀█▀▀█  █▀  ▄▀ █▄▀█ ▄▀ ▀▀▀ ▀ ▀▄ ▀  ▀█▄▄ ▄▄▄▀▀█▀▀▀▀▀████\n" +
                "███   █  ▀▀▄▄▄▀▀ █▄█▄█▀▄▀████ ▀▀ ▀ ▄  ▀█▀▀ ▄██▀▀ ▄▄ ▄▄▀▀████\n" +
                "███▄█▀█  ▀ ▀▀█  ▄▀▀ █▀▀▄ ▀▄█ █ ▄▄▀▄ ▄▀▀▄ ▄▀ ▀▀▀ ▄ ▄▀▄█▀▄████\n" +
                "████▀▄▄██▀▄█▄▄██  ▀ █▀▀▄██▄█▄█▀█  ▀ █▄▄█ █▄█▄█▀██ ▄▄█ ▀▀████\n" +
                "███▀ ▀▀▄ ▀ █▄█▄▀▄█▄▄▀ ▀▀▄█ █▄█▄▀▄  ▄▀ ▀▀▄▄ █▄█▄▀▄██▄▀ ▀▄████\n" +
                "███ ██ ▀▄▀█▄▀▀▄▀▄▀ ▀  █▀▄▄█▄▀██▀▄▀ ▀  ▀▀▄▄█▄▀▀ ▀▄▀ ▀ ██ ████\n" +
                "████▄█ ██▀▄█ ▀ █▄ █▄█▄█ █▀▀█ ▀  █ ▄▄█▄█ █▄▄█ ▀  █▀▀▄█▄█ ████\n" +
                "████ ▄▄▀█▀▄▄▄  ▄▀▀ ▀▄ ▄▄▀▄█▄█▄▄▄▀▀ ▀▄  █▄▄█▄██▄▄▀▀ ▀▄█▀▄████\n" +
                "███  █▄▀▀▀▀▀▀█ █▀▄▀██▄ ▀█▄▀   ▀ ████ █▄███▄▄ ▀  ▀  ▀▀ ▄▀████\n" +
                "███▀ ▄▄ █▀█ ▀██ ▄▄▄█▀▄█ █▀█ █▀█  ▀▀█ █ ▄▄███ █▄ █▀█ ▄▀▄▀████\n" +
                "███▀▄   ▀▀▀  █▀█▀▄▀▄███▄▄█  ▀▀▀ █ ██ ▀▄   █ ▄▀▄ ▀▀▀  █▄█████\n" +
                "███ ▀▄█ █▀ █▀▄█▄▀▄██▀▄ ▄ ▀▀▄▀▄ ▄▀▄ ▀▀▄▀▄  ██▀▄█ ▀▄▀  ▀█ ████\n" +
                "████▄█▄▀▀▀ █▀▀▄▄ ▄ █ █ ▄▀██▄▄█ ▄▀  █▀▀█▄▀█ █▀█ ██▀ ███▀▄████\n" +
                "████▄█ ▄▀▀▄█▄▀███▄█▀▄▀▀▀██  ▄▀█▄█ █▀█▄▀ █▄██▄▀  █▄  ▄▀▄█████\n" +
                "█████▄  ▄▀▄▀ █▀ ▄ ▄▀█ ▄▀▀█▀ ███  ▀▄▀ ▀▀ ▄ ▄▀ ▀▄▀▀█▀ ███ ████\n" +
                "████   ▄█▀▀▄█  ▀▄▀▀▄█ ▀█▄█▀▄██▀ ▄▀▀▀█  █▄███▄██▀▄▄▄██ █▀████\n" +
                "████  █ ▄▀▀▄  ███▄▄▄█ ▀█ ▄▀▄█ ▄█  ▀▄█ ▄██▄▀▄█ ██▀ ▀▄  ▄█████\n" +
                "███▀ █ ▀ ▀ ▄▀█    ▀▀ █ █ ▄▄█ █▀▄ ▀▀  █▄▀ ▀▄  █▄ ▀▄  ▀▀ █████\n" +
                "████▄▄▀██▀▀▀▄▀ ▄▄█▀▀▄  ▄▄▀█▀  ▀▀▀▄▀█▄▄ ████▀▀█▄▀ ▀▀  █▄▄████\n" +
                "███▀▀▀▀▀▀▀█▄▄▄  ▄▀  ███ ▄▀█ █▀█  █  ▄▄▀███▀  █▄ █▀█ ▄ █▀████\n" +
                "███ █▀▀▀█ █▄  ▄▀ ██  ▄▄▀▄ █ ▀▀▀   ▀▄▀███ █▄▄▀▄▀ ▀▀▀ █ ▀▀████\n" +
                "███ █   █ █▄▄ █ ██▄▀▀▀█  █▄▀▄▄█   ▄▀▄██  ▀▄▀▀▀█ ▄ █▀▄█▄█████\n" +
                "███ ▀▀▀▀▀ █▄ ▄█ ▀▄▀▄▀█ ▄▄█  ▀███▀▄ ▄ █ █▀▄ ███▄ ▄█  ▄▀▄█████\n" +
                "████████████████████████████████████████████████████████████\n" +
                "████████████████████████████████████████████████████████████\n" +
                "\n", result);
    }
}