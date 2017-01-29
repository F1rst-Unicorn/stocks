package de.njsm.stocks.client.config;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Properties;

import static de.njsm.stocks.client.config.Configuration.*;
import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

public class ConfigurationTest {

    private Configuration uut;

    @Before
    public void setUp() {
        PropertiesFileHandler handlerMock = mock(PropertiesFileHandler.class);
        uut = new Configuration(handlerMock);
    }

    @Test
    public void canInitialiseFromProperties() {

    }

    private Properties getFilledProperties() {
        Properties result = new Properties();
        String someNumber = "123";
        result.setProperty(SERVER_NAME_CONFIG, "some.host.name");
        result.setProperty(CA_PORT_CONFIG, someNumber);
        result.setProperty(TICKET_PORT_CONFIG, someNumber);
        result.setProperty(SERVER_PORT_CONFIG, someNumber);
        result.setProperty(USER_NAME_CONFIG, "some User");
        result.setProperty(DEVICE_NAME_CONFIG, "some Device");
        result.setProperty(USER_ID_CONFIG, someNumber);
        result.setProperty(DEVICE_ID_CONFIG, someNumber);
        result.setProperty(FINGERPRINT_CONFIG, "randomString");
        return result;
    }

}