package de.njsm.stocks.android.network.server.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import de.njsm.stocks.android.util.Config;
import org.threeten.bp.Instant;

import java.io.IOException;

public class InstantSerialiser extends StdSerializer<Instant> {

    public InstantSerialiser() {
        this(null);
    }

    public InstantSerialiser(Class<Instant> t) {
        super(t);
    }

    @Override
    public void serialize(Instant value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(Config.DATABASE_DATE_FORMAT.format(value));
    }
}
