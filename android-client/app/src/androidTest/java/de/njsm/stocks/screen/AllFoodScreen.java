package de.njsm.stocks.screen;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import de.njsm.stocks.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class AllFoodScreen extends AbstractListPresentingScreen {

    public FoodScreen click(int itemIndex) {
        checkIndex(itemIndex);

        onView(withId(R.id.template_swipe_list_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(itemIndex, ViewActions.click()));

        return new FoodScreen();
    }
}
