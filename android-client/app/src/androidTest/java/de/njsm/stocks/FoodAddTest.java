package de.njsm.stocks;

import android.support.test.rule.ActivityTestRule;
import de.njsm.stocks.frontend.StartupActivity;
import de.njsm.stocks.screen.MainScreen;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

public class FoodAddTest {

    @Rule
    public ActivityTestRule<StartupActivity> mActivityRule = new ActivityTestRule<>(StartupActivity.class);

    @After
    public void tearDown() throws Exception {
        mActivityRule.finishActivity();
    }

    @Test
    public void addFood() throws Exception {
        MainScreen.test()
                .addFoodType("Wine")
                .goToEmptyFood()
                .assertLastItemIsNamed("Wine");
    }
}
