package de.njsm.stocks.screen;

import android.support.test.espresso.matcher.ViewMatchers;
import android.widget.ListView;
import de.njsm.stocks.R;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertTrue;

public class EmptyFoodScreen extends AbstractListPresentingScreen {

    public EmptyFoodScreen assertLastItemContains(String text) {
        int itemCount = getListCount();
        assertTrue(itemCount >= 0);

        int position = itemCount - 1;
        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(position)
                .onChildView(withId(R.id.item_empty_food_outline_name))
                .check(matches(withText(text)));
        return this;

    }
}
