package de.njsm.stocks.client.business.json;

import com.fasterxml.jackson.core.JsonGenerator;
import de.njsm.stocks.client.business.StatusCode;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class StatusCodeSerialiserTest {

    private StatusCodeSerialiser uut;

    private JsonGenerator generatorMock;

    @Before
    public void setup() {
        generatorMock = mock(JsonGenerator.class);
        uut = new StatusCodeSerialiser();
    }

    @Test
    public void statusCodeIsSerialisedWithoutModification() throws Exception {
        StatusCode input = StatusCode.DATABASE_UNREACHABLE;

        uut.serialize(input, generatorMock, null);

        verify(generatorMock).writeNumber(input.ordinal());
    }
}