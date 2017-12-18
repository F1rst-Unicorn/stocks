package de.njsm.stocks.screen;

import android.support.test.espresso.matcher.ViewMatchers;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class FoodScreen extends AbstractListPresentingScreen {

    public FoodScreen assertTitle(String title) {
        onView(withText(title))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        return this;

    }
}
