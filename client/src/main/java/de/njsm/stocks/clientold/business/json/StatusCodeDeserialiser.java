/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.clientold.business.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import de.njsm.stocks.clientold.business.StatusCode;

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
