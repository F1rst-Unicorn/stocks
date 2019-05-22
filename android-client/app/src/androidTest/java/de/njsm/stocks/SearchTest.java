package de.njsm.stocks;


import androidx.test.rule.ActivityTestRule;
import de.njsm.stocks.android.frontend.main.MainActivity;
import de.njsm.stocks.screen.OutlineScreen;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

public class SearchTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @After
    public void tearDown() throws Exception {
        mActivityRule.finishActivity();
    }

    @Test
    public void searchWithoutResult() throws Exception {
        OutlineScreen.test()
                .search("Meat")
                .assertResultCount(0);
    }

    @Test
    public void searchSingleResult() throws Exception {
        String searchText = "Beer";
        OutlineScreen.test()
                .search("Beer")
                .assertResultCount(1)
                .assertItemContent(0, searchText, 2)
                .click(0)
                .assertTitle(searchText);
    }

    @Test
    public void searchMultipleResults() throws Exception {
        String searchText = "ee";
        OutlineScreen.test()
                .search(searchText)
                .assertResultCount(2);
    }
}
