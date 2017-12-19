package de.njsm.stocks.screen;

import android.support.test.espresso.ViewInteraction;
import de.njsm.stocks.R;
import org.hamcrest.Matchers;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static org.hamcrest.core.AllOf.allOf;

public class ServerInputScreen extends AbstractScreen {

    public ServerInputScreen enterServerName(String name) {
        ViewInteraction hostnameTextField = onView(
                allOf(withId(R.id.server_url), isDisplayed()));
        hostnameTextField.perform(replaceText(name), closeSoftKeyboard());
        return this;
    }

    public QrScreen next() {
        ViewInteraction nextScreenButton = onView(
                Matchers.allOf(withId(R.id.stepNext), withText("NEXT"),
                        withParent(Matchers.allOf(withId(R.id.navigation),
                                withParent(withId(R.id.stepSwitcher)))),
                        isDisplayed()));
        nextScreenButton.perform(click());
        return new QrScreen();
    }

    public static ServerInputScreen test() {
        return new ServerInputScreen();
    }
}
