package de.njsm.stocks.screen;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.widget.ListView;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static org.hamcrest.CoreMatchers.*;

public class AllFoodScreen extends AbstractListPresentingScreen {

    public FoodScreen click(int itemIndex) {
        checkIndex(itemIndex);

        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(itemIndex)
                .perform(ViewActions.click());

        return new FoodScreen();
    }
}
