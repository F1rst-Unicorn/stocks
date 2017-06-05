package de.njsm.stocks.client.frontend.cli;

import de.njsm.stocks.client.frontend.cli.service.InputReader;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
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
        verify(reader).next("Please give the URL of the server (localhost): ");
        verifyNoMoreInteractions(reader);
    }

    @Test
    public void realNameIsReturned() throws Exception {
        String input = "someHost";
        when(reader.next(anyString())).thenReturn(input);

        String serverName = uut.getServerName();

        assertEquals(input, serverName);
        verify(reader).next("Please give the URL of the server (localhost): ");
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