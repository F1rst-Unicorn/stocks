package de.njsm.stocks;

import android.support.test.rule.ActivityTestRule;
import de.njsm.stocks.frontend.StartupActivity;
import de.njsm.stocks.screen.FoodScreen;
import de.njsm.stocks.screen.MainScreen;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.threeten.bp.LocalDate;

import java.util.Arrays;

@RunWith(Parameterized.class)
public class FoodItemAddTest {

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
    public void addFoodItems() throws Exception {
        int numberOfItems = index;
        FoodScreen finalScreen = MainScreen.test()
                .goToEmptyFood()
                .click(0)
                .addItems()
                .selectLocation(2)
                .addManyItems(numberOfItems)
                .selectDate(2100, 12, 31)
                .addAndFinish();

        for (int i = 0; i < numberOfItems; i++) {
            finalScreen.assertItem(i, "Jack", "Device", LocalDate.now(), "Cupboard");
        }

        finalScreen.assertItem(numberOfItems, "Jack", "Device", "31.12.00", "Cupboard");

    }
}
