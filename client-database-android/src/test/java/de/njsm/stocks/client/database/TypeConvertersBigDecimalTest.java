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

package de.njsm.stocks.client.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TypeConvertersBigDecimalTest {

    public static List<BigDecimal> input() {
        return Arrays.asList(
                new BigDecimal("0"),
                new BigDecimal("1"),
                new BigDecimal("2"),
                new BigDecimal("9999"),
                new BigDecimal("1.5"),
                new BigDecimal("0.5"),
                new BigDecimal("1000"),
                new BigDecimal("7.77"),
                new BigDecimal("3.14"),
                new BigDecimal("1e3")
        );
    }

    private TypeConverters uut;

    @BeforeEach
    public void setup() {
        this.uut = new TypeConverters();
    }

    @ParameterizedTest
    @MethodSource("input")
    public void converterPreservesIdentity(BigDecimal input) {
        assertEquals(input, uut.dbToBigDecimal(uut.bigDecimalToDb(input)));
    }
}
