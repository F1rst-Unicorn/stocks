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

package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.web.servlet.PrincipalFilter;
import fj.data.Validation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

@RunWith(Parameterized.class)
public class PrincipalFilterParameterizedTest {

    @Parameterized.Parameter
    public String subject;

    @Parameterized.Parameters(name = "{0}")
    public static Iterable<Object[]> getTables() {
        return Arrays.asList(
                new Object[][] {
                        {"/CN=Jack$1$Laptop$2/O=stocks/OU=User/O=stocks"},
                        {"/O=stocks/OU=User/CN=Jack$1$Laptop$2"},
                        {"/CN=Jack$1$Laptop$2"},
                        {"CN=Jack$1$Laptop$2,O=stocks,OU=User,O=stocks"},
                        {"O=stocks,OU=User,CN=Jack$1$Laptop$2"},
                        {"CN=Jack$1$Laptop$2"},
                        {"  CN=Jack$1$Laptop$2,O=stocks,OU=User,O=stocks     "},
                        {"  O=stocks,   OU=User,CN=Jack$1$Laptop$2  "},
                        {"  CN=Jack$1$Laptop$2"},
                        {"  CN=Jack$1$Laptop$2,O=stocks,OU=User,O=stocks    "},
                        {"  O=stocks,  OU=User,CN=Jack$1$Laptop$2   "},
                        {"CN=Jack$1$Laptop$2"},
                });
    }

    @Test
    public void testParsing() {

        Validation<StatusCode, Principals> p = PrincipalFilter.parseSubjectName(subject);

        Assert.assertTrue(p.isSuccess());
        Assert.assertEquals("Jack", p.success().getUsername());
        Assert.assertEquals("Laptop", p.success().getDeviceName());
        Assert.assertEquals(1, p.success().getUid());
        Assert.assertEquals(2, p.success().getDid());
    }

}
