package de.njsm.stocks.servertest;

import de.njsm.stocks.servertest.v1.*;
import de.njsm.stocks.servertest.v2.FoodTest;
import de.njsm.stocks.servertest.v2.LocationTest;
import de.njsm.stocks.servertest.v2.UpdateChangeTest;
import de.njsm.stocks.servertest.v2.EanTest;
import de.njsm.stocks.servertest.v2.RegistrationTest;
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
                     UserTest.class,
                     de.njsm.stocks.servertest.v1.FoodTest.class,
                     FoodTest.class,
                     de.njsm.stocks.servertest.v1.EanTest.class,
                     EanTest.class,
                     FoodItemTest.class,
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
