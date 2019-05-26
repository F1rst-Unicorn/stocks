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
                     de.njsm.stocks.servertest.v1.UpdateChangeTest.class,
                     UpdateChangeTest.class,
                     de.njsm.stocks.servertest.v1.LocationTest.class,
                     LocationTest.class,
                     de.njsm.stocks.servertest.v1.UserTest.class,
                     UserTest.class,
                     de.njsm.stocks.servertest.v1.FoodTest.class,
                     FoodTest.class,
                     de.njsm.stocks.servertest.v1.EanTest.class,
                     EanTest.class,
                     de.njsm.stocks.servertest.v1.FoodItemTest.class,
                     FoodItemTest.class,
                     de.njsm.stocks.servertest.v1.DeviceTest.class,
                     DeviceTest.class,
                     de.njsm.stocks.servertest.v1.RegistrationTest.class,
                     RegistrationTest.class,
                     Cleanup.class,
})
public class TestSuite {

    public static final String HOSTNAME = "dp-server";

    public static final String CA_PORT = "10910";

    public static final String INIT_PORT = "10911";

    public static final String DOMAIN = "https://dp-server:10912";

}
