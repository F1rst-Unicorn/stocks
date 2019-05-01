package de.njsm.stocks.client.business.json;

import com.fasterxml.jackson.core.JsonGenerator;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class InstantSerialiserTest {

    private InstantSerialiser uut;

    private JsonGenerator generatorMock;

    @Before
    public void setup() throws Exception {
        generatorMock = mock(JsonGenerator.class);
        uut = new InstantSerialiser();
    }

    @Test
    public void instantIsSerialisedWithoutModification() throws Exception {
        String expected = "1970.01.01-00:00:00.000-+0000";
        Instant input = Instant.ofEpochMilli(0);

        uut.serialize(input, generatorMock, null);

        verify(generatorMock).writeString(expected);
    }
}