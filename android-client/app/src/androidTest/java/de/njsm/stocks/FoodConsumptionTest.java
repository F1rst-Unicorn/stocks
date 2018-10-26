package de.njsm.stocks;

import android.support.test.rule.ActivityTestRule;
import de.njsm.stocks.frontend.StartupActivity;
import de.njsm.stocks.screen.MainScreen;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

public class FoodConsumptionTest {

    @Rule
    public ActivityTestRule<StartupActivity> mActivityRule = new ActivityTestRule<>(StartupActivity.class);

    @After
    public void tearDown() throws Exception {
        mActivityRule.finishActivity();
    }

    @Test
    public void removeItemsUntilOneIsLeft() throws Exception {
        MainScreen.test()
                .goToEatSoon()
                .click(0)
                .eatAllButOne()
                .assertItem(0, "Jack", "android-client", "31.12.00", "Basement");
    }
}
