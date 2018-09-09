package de.njsm.stocks.server.v2.business.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeParseException;

public class InstantDeserialiser extends StdDeserializer<Instant> {


    protected InstantDeserialiser() {
        this(null);
    }

    public InstantDeserialiser(Class<?> vc) {
        super(vc);
    }

    @Override
    public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        String rawTimestamp = node.asText();
        try {
            return parseString(rawTimestamp);
        } catch (DateTimeParseException e) {
            throw new IOException("Cannot parse date value " + rawTimestamp, e);
        }
    }

    Instant parseString(String rawTimestamp) {
        return InstantSerialiser.FORMAT.parse(rawTimestamp, Instant::from);
    }
}
