package de.njsm.stocks.screen;


import androidx.test.espresso.ViewInteraction;
import de.njsm.stocks.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.core.AllOf.allOf;

public class PrincipalsScreen {

    public PrincipalsScreen assertUser(String name) {
        onView(allOf(withId(R.id.fragment_principals_user_name), isDisplayed()))
                .check(matches(withText(name)));
        return this;
    }

    public PrincipalsScreen assertDevice(String device) {
        onView(allOf(withId(R.id.fragment_principals_device_name), isDisplayed()))
                .check(matches(withText(device)));
        return this;
    }

    public PrincipalsScreen assertUserId(int id) {
        onView(allOf(withId(R.id.fragment_principals_user_id), isDisplayed()))
                .check(matches(withText(String.valueOf(id))));
        return this;
    }

    public PrincipalsScreen assertDeviceId(int id) {
        onView(allOf(withId(R.id.fragment_principals_device_id), isDisplayed()))
                .check(matches(withText(String.valueOf(id))));
        return this;
    }

    public PrincipalsScreen assertFingerPrint(String fingerprint) {
        onView(allOf(withId(R.id.fragment_principals_fingerprint), isDisplayed()))
                .check(matches(withText(fingerprint)));
        return this;
    }

    public PrincipalsScreen assertTicket(String ticket) {
        onView(allOf(withId(R.id.fragment_principals_ticket), isDisplayed()))
                .check(matches(withText(ticket)));
        return this;
    }

    public OutlineScreen submit() {

        ViewInteraction appCompatTextView3 = onView(withId(R.id.fragment_principals_button));
        appCompatTextView3.perform(closeSoftKeyboard(), click());
        return new OutlineScreen();
    }
}
