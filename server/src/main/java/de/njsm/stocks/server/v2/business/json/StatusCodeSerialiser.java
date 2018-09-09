package de.njsm.stocks.server.v2.business.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import de.njsm.stocks.server.v2.business.StatusCode;

import java.io.IOException;

public class StatusCodeSerialiser extends StdSerializer<StatusCode> {


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
