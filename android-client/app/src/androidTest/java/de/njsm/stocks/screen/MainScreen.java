package de.njsm.stocks.screen;

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.view.Gravity;
import de.njsm.stocks.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;

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

    public LocationScreen goToLocations() {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.START)))
                .perform(open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.locations));
        return new LocationScreen();
    }

    public UserScreen goToUsers() {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.START)))
                .perform(open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.users));
        return new UserScreen();
    }

    public EatSoonScreen goToEatSoon() {
        onView(withId(R.id.fragment_outline_cardview)).perform(click());
        return new EatSoonScreen();
    }

    public FoodScreen scanSuccessful() {
        onView(withId(R.id.action_scan)).perform(click());
        return new FoodScreen();
    }

    public AllFoodScreen scanFailing() {
        onView(withId(R.id.action_scan)).perform(click());
        return new AllFoodScreen();
    }

    public static MainScreen test() {
        return new MainScreen();
    }

    public MainScreen assertRegistrationSuccess() {
        onView(withId(android.R.id.message))
                .check(matches(withText(R.string.dialog_finished)));

        ViewInteraction appCompatButton = onView(
                allOf(withId(android.R.id.button1), withText("OK")));
        appCompatButton.perform(scrollTo(), click());
        return this;
    }
}
