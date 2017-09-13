package de.njsm.stocks;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({SetupTest.class,
        InitialDataSetupTest.class,
        FoodAddingTest.class,
        UserAdministrationTest.class,
        RenameEntitiesTest.class,
        FullSyncTest.class,
        FoodConsumptionTest.class
        })
public class SystemTestSuite {
}
