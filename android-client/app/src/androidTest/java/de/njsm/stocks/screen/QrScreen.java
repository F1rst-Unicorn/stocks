package de.njsm.stocks.screen;

import android.support.test.espresso.ViewInteraction;
import de.njsm.stocks.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

public class QrScreen {

    public PrincipalsScreen next() {
        ViewInteraction lockedViewPager = onView(
                allOf(withId(R.id.stepPager), isDisplayed()));
        lockedViewPager.perform(swipeLeft());
        lockedViewPager.perform(swipeLeft());

        return new PrincipalsScreen();
    }
}
