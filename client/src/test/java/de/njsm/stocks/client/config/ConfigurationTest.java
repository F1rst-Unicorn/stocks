package de.njsm.stocks.client.config;

import de.njsm.stocks.client.exceptions.InitialisationException;
import de.njsm.stocks.client.exceptions.InvalidConfigException;
import de.njsm.stocks.client.network.TcpHost;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Properties;

import static de.njsm.stocks.client.config.Configuration.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(JUnitParamsRunner.class)
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
        assertEquals(fingerprint, uut.getFingerprint());
    }

    @Test
    public void readExceptionGivesErrorMessage() throws IOException {
        when(handlerMock.readProperties(anyString())).thenThrow(IOException.class);
        try {
            uut.loadConfig();
        } catch (InitialisationException e) {
            assertEquals("Settings could not be read", e.getMessage());
        }
    }

    @Test
    public void canSaveToProperties() throws Exception {
        Properties values = getFilledProperties();
        Properties valuesUnmodified = (Properties) values.clone();
        when(handlerMock.readProperties(anyString())).thenReturn(values);
        uut.loadConfig();

        uut.saveConfig();

        verify(handlerMock).readProperties(CONFIG_PATH);
        verify(handlerMock).writePropertiesToFile(CONFIG_PATH, values);
        verifyNoMoreInteractions(handlerMock);
        assertEquals(valuesUnmodified, values);
    }

    @Test
    public void writeExceptionGivesErrorMessage() throws IOException, InitialisationException {
        doThrow(IOException.class).when(handlerMock)
                .writePropertiesToFile(anyString(), any());
        when(handlerMock.readProperties(anyString())).thenReturn(getFilledProperties());
        uut.loadConfig();

        try {
            uut.saveConfig();
        } catch (InitialisationException e) {
            assertEquals("Settings could not be saved", e.getMessage());
        }
    }

    @Test
    @Parameters(method = "getValidIntegers")
    public void testIntegerValidationOk(int integerToTest) throws InvalidConfigException {
        uut.validateInt("not used", integerToTest);
    }

    public Object[] getValidIntegers() {
        return new Object[] {
                new Object[] {1},
                new Object[] {2},
                new Object[] {10},
                new Object[] {100},
                new Object[] {Integer.MAX_VALUE},
        };
    }

    @Test
    @Parameters(method = "getInvalidIntegers")
    public void testIntegerValidationFailing(int integerToTest) {
        String key = "key.name";
        try {
            uut.validateInt(key, integerToTest);
            fail();
        } catch (InvalidConfigException e) {
            assertEquals("'" + integerToTest + "' is invalid for " + key,
                    e.getMessage());
        }
    }

    public Object[] getInvalidIntegers() {
        return new Object[] {
                new Object[] {0},
                new Object[] {-2},
                new Object[] {-10},
                new Object[] {-100},
                new Object[] {Integer.MIN_VALUE},
        };
    }

    @Test
    @Parameters(method = "getInvalidProperties")
    public void testSanityCheck(Properties invalidProperties) throws IOException {
        try {
            when(handlerMock.readProperties(anyString())).thenReturn(invalidProperties);
            uut.loadConfig();
            fail();
        } catch (InitialisationException e) {
            assertTrue(e.getMessage().contains("is invalid"));
        }
    }

    public Object[] getInvalidProperties() {
        Object[] result = new Object[13];
        Properties validProperties = getFilledProperties();
        Properties invalidProperties;
        int invalidPort = 0;
        String zero = String.valueOf(invalidPort);
        //assertFalse(TcpHost.isValidPort(invalidPort));

        invalidProperties = (Properties) validProperties.clone();
        invalidProperties.remove(SERVER_NAME_CONFIG);
        result[0] = new Object[] {invalidProperties};

        invalidProperties = (Properties) validProperties.clone();
        invalidProperties.setProperty(SERVER_NAME_CONFIG, "");
        result[1] = new Object[] {invalidProperties};

        invalidProperties = (Properties) validProperties.clone();
        invalidProperties.setProperty(CA_PORT_CONFIG, zero);
        result[2] = new Object[] {invalidProperties};

        invalidProperties = (Properties) validProperties.clone();
        invalidProperties.setProperty(TICKET_PORT_CONFIG, zero);
        result[3] = new Object[] {invalidProperties};

        invalidProperties = (Properties) validProperties.clone();
        invalidProperties.setProperty(SERVER_PORT_CONFIG, zero);
        result[4] = new Object[] {invalidProperties};

        invalidProperties = (Properties) validProperties.clone();
        invalidProperties.remove(USER_NAME_CONFIG);
        result[5] = new Object[] {invalidProperties};

        invalidProperties = (Properties) validProperties.clone();
        invalidProperties.setProperty(USER_NAME_CONFIG, "");
        result[6] = new Object[] {invalidProperties};

        invalidProperties = (Properties) validProperties.clone();
        invalidProperties.remove(DEVICE_NAME_CONFIG);
        result[7] = new Object[] {invalidProperties};

        invalidProperties = (Properties) validProperties.clone();
        invalidProperties.setProperty(DEVICE_NAME_CONFIG, "");
        result[8] = new Object[] {invalidProperties};

        invalidProperties = (Properties) validProperties.clone();
        invalidProperties.remove(FINGERPRINT_CONFIG);
        result[9] = new Object[] {invalidProperties};

        invalidProperties = (Properties) validProperties.clone();
        invalidProperties.setProperty(FINGERPRINT_CONFIG, "");
        result[10] = new Object[] {invalidProperties};

        invalidProperties = (Properties) validProperties.clone();
        invalidProperties.setProperty(USER_ID_CONFIG, zero);
        result[11] = new Object[] {invalidProperties};

        invalidProperties = (Properties) validProperties.clone();
        invalidProperties.setProperty(DEVICE_ID_CONFIG, zero);
        result[12] = new Object[] {invalidProperties};

        return result;
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