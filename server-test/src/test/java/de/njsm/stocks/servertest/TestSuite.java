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

package de.njsm.stocks.servertest;

import de.njsm.stocks.servertest.v2.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({SetupTest.class,
                     InvalidAccessTest.class,
                     InitialServerTest.class,
                     UpdateChangeTest.class,
                     HealthTest.class,
                     LocationTest.class,
                     UserTest.class,
                     FoodTest.class,
                     EanTest.class,
                     FoodItemTest.class,
                     DeviceTest.class,
                     RegistrationTest.class,
                     Cleanup.class,
})
public class TestSuite {

    public static final String HOSTNAME = "dp-server";

    public static final String CA_PORT = "10910";

    public static final String INIT_PORT = "10911";

    public static final String DOMAIN = "https://dp-server:10912";

}
