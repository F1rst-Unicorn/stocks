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

package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.web.servlet.PrincipalFilter;
import fj.data.Validation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrincipalFilterParameterizedTest {

    public static Stream<String> getTables() {
        return Stream.of(
                "/CN=Jack$1$Laptop$2/O=stocks/OU=User/O=stocks",
                "/O=stocks/OU=User/CN=Jack$1$Laptop$2",
                "/CN=Jack$1$Laptop$2",
                "CN=Jack$1$Laptop$2,O=stocks,OU=User,O=stocks",
                "O=stocks,OU=User,CN=Jack$1$Laptop$2",
                "CN=Jack$1$Laptop$2",
                "  CN=Jack$1$Laptop$2,O=stocks,OU=User,O=stocks     ",
                "  O=stocks,   OU=User,CN=Jack$1$Laptop$2  ",
                "  CN=Jack$1$Laptop$2",
                "  CN=Jack$1$Laptop$2,O=stocks,OU=User,O=stocks    ",
                "  O=stocks,  OU=User,CN=Jack$1$Laptop$2   ",
                "CN=Jack$1$Laptop$2"
        );
    }

    @ParameterizedTest
    @MethodSource("getTables")
    public void testParsing(String subject) {

        Validation<StatusCode, Principals> p = PrincipalFilter.parseSubjectName(subject);

        assertTrue(p.isSuccess());
        assertEquals("Jack", p.success().getUsername());
        assertEquals("Laptop", p.success().getDeviceName());
        assertEquals(1, p.success().getUid());
        assertEquals(2, p.success().getDid());
    }

}
