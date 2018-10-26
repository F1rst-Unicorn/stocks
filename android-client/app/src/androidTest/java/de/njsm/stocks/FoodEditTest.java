package de.njsm.stocks;

import android.support.test.rule.ActivityTestRule;
import de.njsm.stocks.frontend.StartupActivity;
import de.njsm.stocks.screen.MainScreen;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

public class FoodEditTest {

    @Rule
    public ActivityTestRule<StartupActivity> mActivityRule = new ActivityTestRule<>(StartupActivity.class);

    @After
    public void tearDown() throws Exception {
        mActivityRule.finishActivity();
    }

    @Test
    public void editLastItemInList() throws Exception {
        MainScreen.test()
                .goToEatSoon()
                .click(0)
                .assertLastItem("Jack", "android-client", "31.12.00", "Basement")
                .longClickLast()
                .assertLocation("Basement")
                .assertDate(2100, 12, 31)
                .selectDate(2099, 12, 31)
                .addAndFinish()
                .assertLastItem("Jack", "android-client", "31.12.99", "Basement")
                .longClickLast()
                .assertLocation("Basement")
                .assertDate(2099, 12, 31)
                .selectDate(2100, 12, 31)
                .addAndFinish()
                .assertLastItem("Jack", "android-client", "31.12.00", "Basement");
    }

    @Test
    public void verifyMaximumLocationIsSelected() throws Exception {
        MainScreen.test()
                .goToEatSoon()
                .click(0)
                .addItems()
                .assertLocation("Basement");
    }

    @Test
    public void pretendEditingButPressBack() throws Exception {
        MainScreen.test()
                .goToEatSoon()
                .click(0)
                .assertLastItem("Jack", "android-client", "31.12.00", "Basement")
                .longClickLast()
                .pressBack()
                .assertLastItem("Jack", "android-client", "31.12.00", "Basement");
    }
}
