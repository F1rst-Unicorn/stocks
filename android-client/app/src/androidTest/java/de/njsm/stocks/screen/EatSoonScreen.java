package de.njsm.stocks.screen;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.widget.ListView;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.IsAnything.anything;

public class EatSoonScreen extends AbstractListPresentingScreen {

    public FoodScreen click(int index) {
        checkIndex(index);

        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(index)
                .perform(ViewActions.click());
        return new FoodScreen();
    }
}
