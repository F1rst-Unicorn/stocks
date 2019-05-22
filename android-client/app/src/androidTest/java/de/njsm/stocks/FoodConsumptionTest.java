package de.njsm.stocks;


import androidx.test.rule.ActivityTestRule;
import de.njsm.stocks.android.frontend.main.MainActivity;
import de.njsm.stocks.screen.OutlineScreen;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

public class FoodConsumptionTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @After
    public void tearDown() throws Exception {
        mActivityRule.finishActivity();
    }

    @Test
    public void removeItemsUntilOneIsLeft() throws Exception {
        OutlineScreen.test()
                .goToEatSoon()
                .click(0)
                .eatAllButOne()
                .assertItem(0, "Jack", "android-client", "31.12.00", "Basement");
    }
}
