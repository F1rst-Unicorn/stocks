/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.stocks.common.data.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.threeten.bp.Instant;
import org.threeten.bp.format.DateTimeParseException;
import org.threeten.bp.temporal.TemporalAccessor;
import org.threeten.bp.temporal.TemporalQuery;

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
        JsonNode node = p.getCodec().readTree(p);
        String rawTimestamp = node.asText();
        try {
            return parseString(rawTimestamp);
        } catch (DateTimeParseException e) {
            throw new IOException("Cannot parse date value " + rawTimestamp, e);
        }
    }

    Instant parseString(String rawTimestamp) {
        return InstantSerialiser.FORMAT.parse(rawTimestamp, new TemporalQuery<Instant>() {
            @Override
            public Instant queryFrom(TemporalAccessor temporal) {
                return Instant.from(temporal);
            }
        });
    }
}
