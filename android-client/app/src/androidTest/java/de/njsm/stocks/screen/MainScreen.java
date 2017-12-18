package de.njsm.stocks.screen;

import de.njsm.stocks.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class MainScreen {

    public SearchScreen search(String searchText) {
        onView(withId(R.id.action_search)).perform(click());
        onView(withId(R.id.search_src_text)).perform(replaceText(searchText));
        onView(withId(R.id.search_go_btn)).perform(click());

        return new SearchScreen();
    }

    public static MainScreen test() {
        return new MainScreen();
    }
}
