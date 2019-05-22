package de.njsm.stocks.screen;

import android.view.Gravity;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.NavigationViewActions;
import de.njsm.stocks.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerActions.open;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;

public class OutlineScreen extends AbstractScreen {

    public SearchScreen search(String searchText) {
        onView(withId(R.id.fragment_outline_options_search)).perform(click());
        onView(withId(R.id.search_src_text)).perform(replaceText(searchText));
        onView(withId(R.id.search_go_btn)).perform(click());

        return new SearchScreen();
    }

    public OutlineScreen addFoodType(String name) {
        onView(withId(R.id.fragment_outline_fab)).perform(click());
        onView(withHint(R.string.hint_food)).perform(replaceText(name));
        onView(withText("OK")).perform(click());
        return this;
    }

    public EmptyFoodScreen goToEmptyFood() {
        onView(withId(R.id.fragment_outline_cardview2)).perform(click());
        return new EmptyFoodScreen();
    }

    public LocationScreen goToLocations() {
        onView(withId(R.id.main_drawer_layout))
                .check(matches(isClosed(Gravity.START)))
                .perform(open());
        onView(withId(R.id.main_nav)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_locations));
        return new LocationScreen();
    }

    public UserScreen goToUsers() {
        onView(withId(R.id.main_drawer_layout))
                .check(matches(isClosed(Gravity.START)))
                .perform(open());
        onView(withId(R.id.main_nav)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_users));
        return new UserScreen();
    }

    public EatSoonScreen goToEatSoon() {
        onView(withId(R.id.fragment_outline_cardview)).perform(click());
        return new EatSoonScreen();
    }

    public FoodScreen scanSuccessful() {
        onView(withId(R.id.fragment_outline_options_scan)).perform(click());
        sleep(500);
        return new FoodScreen();
    }

    public AllFoodScreen scanFailing() {
        onView(withId(R.id.fragment_outline_options_scan)).perform(click());
        sleep(500);
        return new AllFoodScreen();
    }

    public static OutlineScreen test() {
        return new OutlineScreen();
    }

    public OutlineScreen assertRegistrationSuccess() {
        onView(withId(android.R.id.message))
                .check(matches(withText(R.string.dialog_finished)));

        ViewInteraction appCompatButton = onView(
                allOf(withId(android.R.id.button1), withText("OK")));
        appCompatButton.perform(scrollTo(), click());
        return this;
    }
}
