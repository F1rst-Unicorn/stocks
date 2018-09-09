package de.njsm.stocks.server.v2.business.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import de.njsm.stocks.server.v2.business.StatusCode;

import java.io.IOException;

public class StatusCodeDeserialiser extends StdDeserializer<StatusCode> {

    public StatusCodeDeserialiser(Class<?> vc) {
        super(vc);
    }

    @Override
    public StatusCode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        int code = node.asInt();
        if (code < 0 || code >= StatusCode.values().length) {
            throw new IOException("Invalid status code " + code);
        }

        return StatusCode.values()[code];
    }
}
