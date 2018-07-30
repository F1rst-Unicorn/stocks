package de.njsm.stocks.servertest;

import de.njsm.stocks.servertest.v2.FoodTest;
import de.njsm.stocks.servertest.v1.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({SetupTest.class,
                     InvalidAccessTest.class,
                     InitialServerTest.class,
                     UpdateChangeTest.class,
                     LocationTest.class,
                     UserTest.class,
                     de.njsm.stocks.servertest.v1.FoodTest.class,
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
