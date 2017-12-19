package de.njsm.stocks.screen;

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
import de.njsm.stocks.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;

public class PrincipalsScreen {

    public PrincipalsScreen assertUser(String name) {
        onView(allOf(withId(R.id.user_name), isDisplayed()))
                .check(matches(ViewMatchers.withText(name)));
        return this;
    }

    public PrincipalsScreen assertDevice(String device) {
        onView(allOf(withId(R.id.device_name), isDisplayed()))
                .check(matches(ViewMatchers.withText(device)));
        return this;
    }

    public PrincipalsScreen assertUserId(int id) {
        onView(allOf(withId(R.id.user_id), isDisplayed()))
                .check(matches(ViewMatchers.withText(String.valueOf(id))));
        return this;
    }

    public PrincipalsScreen assertDeviceId(int id) {
        onView(allOf(withId(R.id.device_id), isDisplayed()))
                .check(matches(ViewMatchers.withText(String.valueOf(id))));
        return this;
    }

    public PrincipalsScreen assertFingerPrint(String fingerprint) {
        onView(allOf(withId(R.id.fingerprint), isDisplayed()))
                .check(matches(ViewMatchers.withText(fingerprint)));
        return this;
    }

    public PrincipalsScreen assertTicket(String ticket) {
        onView(allOf(withId(R.id.ticket), isDisplayed()))
                .check(matches(ViewMatchers.withText(ticket)));
        return this;
    }

    public MainScreen submit() {

        ViewInteraction appCompatTextView3 = onView(
                allOf(withId(R.id.stepEnd), withText("COMPLETE"),
                        withParent(allOf(withId(R.id.navigation),
                                withParent(withId(R.id.stepSwitcher)))),
                        isDisplayed()));
        appCompatTextView3.perform(closeSoftKeyboard(), click());
        return new MainScreen();
    }
}
