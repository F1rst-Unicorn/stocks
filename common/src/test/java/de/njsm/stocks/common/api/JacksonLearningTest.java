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

package de.njsm.stocks.common.api;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JacksonLearningTest {

    @Test
    public void getterWithoutPrefixWorks() throws JsonProcessingException {
        String output = new ObjectMapper().writeValueAsString(new Data());

        assertEquals("{\"data\":1109}", output);
    }

    @Test
    public void canDeserialiseListResponse() throws JsonProcessingException {
        ListResponse<Integer> output = new ObjectMapper().readValue("{\"status\":0,\"data\":[]}", ListResponse.class);

        assertEquals(StatusCode.SUCCESS, output.getStatus());
        assertEquals(Collections.emptyList(), output.getData());
    }

    private static final class Data {
        @JsonGetter
        public int data() {
            return 1109;
        }
    }
}
