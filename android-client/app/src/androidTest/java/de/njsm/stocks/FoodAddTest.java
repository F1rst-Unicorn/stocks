package de.njsm.stocks;

import android.support.test.rule.ActivityTestRule;
import de.njsm.stocks.frontend.StartupActivity;
import de.njsm.stocks.screen.MainScreen;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

@RunWith(Parameterized.class)
public class FoodAddTest {

    @Rule
    public ActivityTestRule<StartupActivity> mActivityRule = new ActivityTestRule<>(StartupActivity.class);

    @Parameterized.Parameter
    public String foodName;

    @Parameterized.Parameters(name = "Food {0}")
    public static Iterable<Object[]> getFoodNames() {
        return Arrays.asList(
                new Object[][] {
                        {"Beer"},
                        {"Carrot"},
                        {"Bread"},
                        {"Pepper"},
                        {"Cheese"}
                });
    }

    @Test
    public void addFood() throws Exception {
        MainScreen.test()
                .addFoodType(foodName)
                .goToEmptyFood()
                .assertLastItemIsNamed(foodName);
    }
}
