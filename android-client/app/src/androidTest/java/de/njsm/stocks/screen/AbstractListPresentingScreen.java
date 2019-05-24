package de.njsm.stocks.screen;


import de.njsm.stocks.R;
import de.njsm.stocks.util.StealCountAction;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.fail;

public class AbstractListPresentingScreen extends AbstractScreen {

    private int listId;

    public AbstractListPresentingScreen() {
        this(R.id.template_swipe_list_list);
    }

    public AbstractListPresentingScreen(int listId) {
        this.listId = listId;
    }

    protected void checkIndex(int itemIndex) {
        int count = getListCount();
        if (itemIndex < 0 || itemIndex >= count) {
            fail("index " + itemIndex + " is not in valid range [0," + count + ")");
        }
    }

    protected int getListCount() {
        return getListCount(listId);
    }

    protected int getListCount(int listId) {
        StealCountAction stealCountAction = new StealCountAction();
        onView(withId(listId)).perform(stealCountAction);
        return stealCountAction.getCount();
    }

}
