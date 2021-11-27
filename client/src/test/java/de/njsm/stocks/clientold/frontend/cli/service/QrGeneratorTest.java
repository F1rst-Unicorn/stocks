/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.clientold.frontend.cli.service;

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
