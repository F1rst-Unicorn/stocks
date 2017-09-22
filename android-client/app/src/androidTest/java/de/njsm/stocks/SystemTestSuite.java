package de.njsm.stocks;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({SetupTest.class,
        FoodAddTest.class,
        LocationAddTest.class,
        FoodItemAddTest.class,
        UserAdministrationTest.class,
        SearchTest.class,
        FoodConsumptionTest.class
        })
public class SystemTestSuite {
}
