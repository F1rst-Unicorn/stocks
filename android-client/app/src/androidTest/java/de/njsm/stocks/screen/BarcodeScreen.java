package de.njsm.stocks.screen;

import android.support.test.espresso.matcher.ViewMatchers;
import android.widget.ListView;
import de.njsm.stocks.R;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.IsAnything.anything;
import static org.junit.Assert.assertEquals;

public class BarcodeScreen extends AbstractListPresentingScreen {

    public BarcodeScreen recordNewBarcode() {
        onView(withId(R.id.fab)).perform(click());
        return this;
    }

    public BarcodeScreen deleteBarcode(int index) {
        checkIndex(index);

        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(index)
                .perform(longClick());
        onView(withText("OK")).perform(click());
        return this;
    }

    public BarcodeScreen assertItemCount(int count) {
        assertEquals(count, getListCount());
        return this;
    }
}
