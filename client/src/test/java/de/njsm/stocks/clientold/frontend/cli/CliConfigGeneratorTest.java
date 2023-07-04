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

package de.njsm.stocks.clientold.frontend.cli;

import de.njsm.stocks.clientold.frontend.cli.service.InputReader;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CliConfigGeneratorTest {

    private CliConfigGenerator uut;

    private InputReader reader;

    @Before
    public void setup() throws Exception {
        reader = mock(InputReader.class);
        uut = new CliConfigGenerator(reader);
    }

    @Test
    public void emptyStringGivesDefault() throws Exception {
        when(reader.next(anyString())).thenReturn("");

        String serverName = uut.getServerName();

        assertEquals("localhost", serverName);
        verify(reader).next("Please give the hostname of the server (localhost): ");
        verifyNoMoreInteractions(reader);
    }

    @Test
    public void realNameIsReturned() throws Exception {
        String input = "someHost";
        when(reader.next(anyString())).thenReturn(input);

        String serverName = uut.getServerName();

        assertEquals(input, serverName);
        verify(reader).next("Please give the hostname of the server (localhost): ");
        verifyNoMoreInteractions(reader);
    }

    @Test
    public void verifyDefaultPorts() throws Exception {
        int caPort = 10910;
        int sentryPort = 10911;
        int serverPort = 10912;
        String caPrompt = "Please give the CA port of the server";
        String sentryPrompt = "Please give the ticket server port of the server";
        String serverPrompt = "Please give the main server port of the server";
        when(reader.nextInt(caPrompt, caPort)).thenReturn(caPort);
        when(reader.nextInt(sentryPrompt, sentryPort)).thenReturn(sentryPort);
        when(reader.nextInt(serverPrompt, serverPort)).thenReturn(serverPort);

        int[] result = uut.getPorts();

        assertEquals(caPort, result[0]);
        assertEquals(sentryPort, result[1]);
        assertEquals(serverPort, result[2]);
        verify(reader, times(1)).nextInt(caPrompt, caPort);
        verify(reader, times(1)).nextInt(sentryPrompt, sentryPort);
        verify(reader, times(1)).nextInt(serverPrompt, serverPort);
        verifyNoMoreInteractions(reader);
    }
}
