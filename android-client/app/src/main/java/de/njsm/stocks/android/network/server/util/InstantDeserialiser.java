package de.njsm.stocks.android.network.server.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import de.njsm.stocks.android.util.Config;
import org.threeten.bp.Instant;
import org.threeten.bp.format.DateTimeParseException;

import java.io.IOException;

public class InstantDeserialiser extends StdDeserializer<Instant> {


    protected InstantDeserialiser() {
        this(null);
    }

    public InstantDeserialiser(Class<?> vc) {
        super(vc);
    }

    @Override
    public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return parseString(((JsonNode) p.getCodec().readTree(p)).asText());
    }

    public static Instant parseString(String rawTimestamp) throws IOException {
        try {
            return Config.DATABASE_DATE_FORMAT.parse(rawTimestamp, Instant::from);
        } catch (DateTimeParseException |
                NullPointerException e) {
            throw new IOException("Cannot parse date value " + rawTimestamp, e);
        }
    }
}
