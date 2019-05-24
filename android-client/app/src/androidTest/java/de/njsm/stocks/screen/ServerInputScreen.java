package de.njsm.stocks.screen;

import androidx.test.espresso.ViewInteraction;
import de.njsm.stocks.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.AllOf.allOf;

public class ServerInputScreen extends AbstractScreen {

    public ServerInputScreen enterServerName(String name) {
        ViewInteraction hostnameTextField;
        hostnameTextField = onView(
                allOf(withId(R.id.fragment_server_url), isDisplayed()));
        hostnameTextField.perform(replaceText(name), closeSoftKeyboard());
        return this;
    }

    public QrScreen next() {
        ViewInteraction nextScreenButton = onView(withId(R.id.fragment_server_server_button));
        nextScreenButton.perform(click());
        return new QrScreen();
    }

    public static ServerInputScreen test() {
        return new ServerInputScreen();
    }
}
