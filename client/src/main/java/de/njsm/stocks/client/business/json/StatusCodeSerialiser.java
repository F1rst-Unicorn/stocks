package de.njsm.stocks.client.business.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import de.njsm.stocks.client.business.StatusCode;

import java.io.IOException;

public class StatusCodeSerialiser extends StdSerializer<StatusCode> {


    public StatusCodeSerialiser() {
        this(null);
    }

    public StatusCodeSerialiser(Class<StatusCode> t) {
        super(t);
    }

    @Override
    public void serialize(StatusCode value,
                          JsonGenerator gen,
                          SerializerProvider provider) throws IOException {
        gen.writeNumber(value.ordinal());
    }
}
