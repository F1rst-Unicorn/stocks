package de.njsm.stocks.client.business.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class InstantSerialiser extends StdSerializer<Instant> {

    static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd-HH:mm:ss.SSS-Z")
            .withZone(ZoneId.of("UTC"));

    public InstantSerialiser() {
        this(null);
    }

    public InstantSerialiser(Class<Instant> t) {
        super(t);
    }

    @Override
    public void serialize(Instant value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(FORMAT.format(value));
    }
}
