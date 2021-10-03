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

package de.njsm.stocks.common.api.serialisers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.njsm.stocks.common.api.RecipeForGetting;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class DurationDeserialiserTest {
    @Test
    public void deserialisingWorks() throws JsonProcessingException {
        String input = "{\"id\":1,\"version\":0,\"name\":\"name\"," +
                "\"duration\":\"PT2H\",\"instructions\":\"\"}";

        RecipeForGetting output = new ObjectMapper().readValue(input, RecipeForGetting.class);

        assertEquals(Duration.ZERO.plusHours(2), output.duration());
    }
}
