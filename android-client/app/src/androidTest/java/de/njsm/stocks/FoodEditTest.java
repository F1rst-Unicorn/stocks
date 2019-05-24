package de.njsm.stocks;


import androidx.test.rule.ActivityTestRule;
import de.njsm.stocks.android.frontend.main.MainActivity;
import de.njsm.stocks.screen.OutlineScreen;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

public class FoodEditTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @After
    public void tearDown() throws Exception {
        mActivityRule.finishActivity();
    }

    @Test
    public void editLastItemInList() throws Exception {
        OutlineScreen.test()
                .goToEatSoon()
                .click(0)
                .assertLastItem("Jack", "android-client", "31.12.00", "Basement")
                .longClickLast()
                .assertLocation("Basement")
                .assertDate(2100, 12, 31)
                .selectDate(2099, 12, 31)
                .editItem()
                .assertLastItem("Jack", "android-client", "31.12.99", "Basement")
                .longClickLast()
                .assertLocation("Basement")
                .assertDate(2099, 12, 31)
                .selectDate(2100, 12, 31)
                .editItem()
                .assertLastItem("Jack", "android-client", "31.12.00", "Basement");
    }

    @Test
    public void verifyMaximumLocationIsSelected() throws Exception {
        OutlineScreen.test()
                .goToEatSoon()
                .click(0)
                .addItems()
                .assertLocation("Basement");
    }

    @Test
    public void pretendEditingButPressBack() throws Exception {
        OutlineScreen.test()
                .goToEatSoon()
                .click(0)
                .assertLastItem("Jack", "android-client", "31.12.00", "Basement")
                .longClickLast()
                .pressBack()
                .assertLastItem("Jack", "android-client", "31.12.00", "Basement");
    }
}
