package de.njsm.stocks;


import androidx.test.rule.ActivityTestRule;
import de.njsm.stocks.android.frontend.main.MainActivity;
import de.njsm.stocks.screen.FoodScreen;
import de.njsm.stocks.screen.OutlineScreen;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.threeten.bp.LocalDate;

public class FoodItemAddTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @After
    public void tearDown() throws Exception {
        mActivityRule.finishActivity();
    }

    @Test
    public void addFoodItems() throws Exception {
        int numberOfItems = 1;
        FoodScreen finalScreen = OutlineScreen.test()
                .goToEmptyFood()
                .click(0)
                .addItems()
                .selectLocation(2)
                .assertLocation("Basement")
                .addManyItems(numberOfItems)
                .selectDate(2100, 12, 31)
                .addAndFinish();

        for (int i = 0; i < numberOfItems; i++) {
            finalScreen.assertItem(i, "Jack", "android-client", LocalDate.now(), "Basement");
        }

        finalScreen.assertItem(numberOfItems, "Jack", "android-client", "31.12.00", "Basement");

    }
}
