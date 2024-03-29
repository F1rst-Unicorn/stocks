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

package de.njsm.stocks.common.api.serialisers;

import com.fasterxml.jackson.core.JsonGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Period;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PeriodSerialiserTest {

    private PeriodSerialiser uut;

    private JsonGenerator generatorMock;

    @BeforeEach
    public void setup() {
        generatorMock = mock(JsonGenerator.class);
        uut = new PeriodSerialiser();
    }

    @Test
    public void statusCodeIsSerialisedWithoutModification() throws Exception {
        Period input = Period.ofDays(1);

        uut.serialize(input, generatorMock, null);

        verify(generatorMock).writeNumber(1);
    }
}
