package de.njsm.stocks;

import android.support.test.rule.ActivityTestRule;
import de.njsm.stocks.frontend.StartupActivity;
import de.njsm.stocks.screen.FoodScreen;
import de.njsm.stocks.screen.MainScreen;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.threeten.bp.LocalDate;

public class FoodItemAddTest {

    @Rule
    public ActivityTestRule<StartupActivity> mActivityRule = new ActivityTestRule<>(StartupActivity.class);

    @After
    public void tearDown() throws Exception {
        mActivityRule.finishActivity();
    }

    @Test
    public void addFoodItems() throws Exception {
        int numberOfItems = 1;
        FoodScreen finalScreen = MainScreen.test()
                .goToEmptyFood()
                .click(0)
                .addItems()
                .selectLocation(2)
                .assertLocation("Basement")
                .addManyItems(numberOfItems)
                .selectDate(2100, 12, 31)
                .addAndFinish();

        for (int i = 0; i < numberOfItems; i++) {
            finalScreen.assertItem(i, "Jack", "Device", LocalDate.now(), "Basement");
        }

        finalScreen.assertItem(numberOfItems, "Jack", "Device", "31.12.00", "Basement");

    }
}
