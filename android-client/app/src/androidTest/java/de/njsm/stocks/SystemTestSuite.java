package de.njsm.stocks;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({SetupTest.class,
        FoodAddTest.class,
        LocationAddTest.class,
        FoodItemAddTest.class,
        EanAdminTest.class,
        SearchTest.class,
        EanScanTest.class,
        UserAdministrationTest.class,
        FoodEditTest.class,
        FoodConsumptionTest.class
        })
public class SystemTestSuite {

    public static final int LOOP_BREAKER = 30;
}
