package de.njsm.stocks.screen;

import de.njsm.stocks.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class MainScreen extends AbstractScreen {

    public SearchScreen search(String searchText) {
        onView(withId(R.id.action_search)).perform(click());
        onView(withId(R.id.search_src_text)).perform(replaceText(searchText));
        onView(withId(R.id.search_go_btn)).perform(click());

        return new SearchScreen();
    }

    public MainScreen addFoodType(String name) {
        onView(withId(R.id.fab)).perform(click());
        onView(withHint(R.string.hint_food)).perform(replaceText(name));
        onView(withText("OK")).perform(click());
        return this;
    }

    public EmptyFoodScreen goToEmptyFood() {
        onView(withId(R.id.fragment_outline_cardview2)).perform(click());
        return new EmptyFoodScreen();
    }

    public static MainScreen test() {
        return new MainScreen();
    }
}
