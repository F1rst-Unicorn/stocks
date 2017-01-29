package de.njsm.stocks.client.config;

import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static de.njsm.stocks.client.config.Configuration.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ConfigurationTest {

    private Configuration uut;
    private PropertiesFileHandler handlerMock;

    private String serverName = "some.host.name";
    private String userName = "some user";
    private String deviceName = "some device";
    private String fingerprint = "randomString";
    private String exampleId = "1";
    private String examplePort = "10910";
    private int exampleIdInt = 1;
    private int examplePortInt = 10910;

    @Before
    public void setUp() {
        handlerMock = mock(PropertiesFileHandler.class);
        uut = new Configuration(handlerMock);
    }

    @Test
    public void canInitialiseFromProperties() throws Exception {
        when(handlerMock.readProperties(anyString())).thenReturn(getFilledProperties());

        uut.loadConfig();

        assertEquals(serverName, uut.getServerName());
        assertEquals(userName, uut.getUsername());
        assertEquals(deviceName, uut.getDeviceName());
        assertEquals(examplePortInt, uut.getCaPort());
        assertEquals(examplePortInt, uut.getTicketPort());
        assertEquals(examplePortInt, uut.getServerPort());
        assertEquals(exampleIdInt, uut.getUserId());
        assertEquals(exampleIdInt, uut.getDeviceId());
    }

    @Test
    public void canSaveToProperties() throws Exception {
        Properties values = getFilledProperties();
        when(handlerMock.readProperties(anyString())).thenReturn(values);
        uut.loadConfig();

        uut.saveConfig();

        verify(handlerMock).writePropertiesToFile(CONFIG_PATH, values);
    }

    private Properties getFilledProperties() {
        Properties result = new Properties();
        result.setProperty(SERVER_NAME_CONFIG, serverName);
        result.setProperty(CA_PORT_CONFIG, examplePort);
        result.setProperty(TICKET_PORT_CONFIG, examplePort);
        result.setProperty(SERVER_PORT_CONFIG, examplePort);
        result.setProperty(USER_NAME_CONFIG, userName);
        result.setProperty(DEVICE_NAME_CONFIG, deviceName);
        result.setProperty(USER_ID_CONFIG, exampleId);
        result.setProperty(DEVICE_ID_CONFIG, exampleId);
        result.setProperty(FINGERPRINT_CONFIG, fingerprint);
        return result;
    }

}