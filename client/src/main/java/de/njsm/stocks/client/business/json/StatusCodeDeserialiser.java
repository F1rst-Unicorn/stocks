package de.njsm.stocks.client.business.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import de.njsm.stocks.client.business.StatusCode;

import java.io.IOException;

public class StatusCodeDeserialiser extends StdDeserializer<StatusCode> {

    protected StatusCodeDeserialiser() {
        this(null);
    }

    public StatusCodeDeserialiser(Class<?> vc) {
        super(vc);
    }

    @Override
    public StatusCode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return parseCode(((JsonNode) p.getCodec().readTree(p)).asInt());
    }

    StatusCode parseCode(int code) throws IOException {
        if (code < 0 || code >= StatusCode.values().length) {
            throw new IOException("Invalid status code " + code);
        }
        return StatusCode.values()[code];
    }
}
