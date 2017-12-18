package de.njsm.stocks.screen;

import de.njsm.stocks.util.StealCountAction;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.fail;

public class AbstractListPresentingScreen extends AbstractScreen {

    protected void checkIndex(int itemIndex) {
        int count = getListCount();
        if (itemIndex < 0 || itemIndex >= count) {
            fail("index " + itemIndex + " is not in valid range [0," + count + "]");
        }
    }

    protected int getListCount() {
        StealCountAction stealCountAction = new StealCountAction();
        onView(withId(android.R.id.list)).perform(stealCountAction);
        return stealCountAction.getCount();
    }

}
