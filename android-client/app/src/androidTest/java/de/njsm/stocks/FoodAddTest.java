package de.njsm.stocks;


import androidx.test.rule.ActivityTestRule;
import de.njsm.stocks.android.frontend.main.MainActivity;
import de.njsm.stocks.screen.OutlineScreen;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

public class FoodAddTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @After
    public void tearDown() throws Exception {
        mActivityRule.finishActivity();
    }

    @Test
    public void addFood() throws Exception {
        OutlineScreen.test()
                .addFoodType("Wine")
                .goToEmptyFood()
                .assertLastItemIsNamed("Wine");
    }
}
