package de.njsm.stocks;

import android.support.test.rule.ActivityTestRule;
import de.njsm.stocks.frontend.StartupActivity;
import de.njsm.stocks.screen.MainScreen;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

public class SearchTest {

    @Rule
    public ActivityTestRule<StartupActivity> mActivityRule = new ActivityTestRule<>(StartupActivity.class);

    @After
    public void tearDown() throws Exception {
        mActivityRule.finishActivity();
    }

    @Test
    public void searchWithoutResult() throws Exception {
        MainScreen.test()
                .search("Meat")
                .assertResultCount(0);
    }

    @Test
    public void searchSingleResult() throws Exception {
        String searchText = "Beer";
        MainScreen.test()
                .search("Beer")
                .assertResultCount(1)
                .assertItemContent(0, searchText, 1)
                .click(0)
                .assertTitle(searchText);
    }

    @Test
    public void searchMultipleResults() throws Exception {
        String searchText = "e";
        MainScreen.test()
                .search(searchText)
                .assertResultCount(2);
    }
}
