package de.njsm.stocks;

import android.support.test.rule.ActivityTestRule;
import de.njsm.stocks.frontend.StartupActivity;
import de.njsm.stocks.screen.MainScreen;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.Parameterized;

import java.util.Arrays;

public class FoodConsumptionTest {

    @Rule
    public ActivityTestRule<StartupActivity> mActivityRule = new ActivityTestRule<>(StartupActivity.class);

    @Parameterized.Parameter
    public int index;

    @Parameterized.Parameters(name = "Food at position {0}")
    public static Iterable<Object[]> getFoodNames() {
        return Arrays.asList(
                new Object[][] {
                        {0},
                        {1},
                        {2},
                        {3},
                        {4}
                });
    }

    @Test
    public void removeItemsUntilOneIsLeft() throws Exception {
        MainScreen.test()
                .goToEatSoon()
                .click(0)
                .eatAllButOne()
                .assertItem(0, "Jack", "Device", "31.12.00", "Cupboard");
    }
}
