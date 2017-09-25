package de.njsm.stocks;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.widget.ListView;
import de.njsm.stocks.frontend.StartupActivity;
import de.njsm.stocks.util.StealCountAction;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.IsAnything.anything;
import static org.junit.Assert.assertEquals;

public class EanAdminTest {

    @Rule
    public ActivityTestRule<StartupActivity> mActivityRule = new ActivityTestRule<>(StartupActivity.class);

    @Before
    public void setup() throws Exception {
        Intents.init();
        Intent data = new Intent();
        data.putExtra("SCAN_RESULT", "1234567891234");
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, data);
        intending(toPackage("com.google.zxing.client.android")).respondWith(result);
    }

    @After
    public void tearDown() throws Exception {
        Intents.release();
    }

    @Test
    public void addEanNumbersAndRemoveOne() throws Exception {
        onView(withId(R.id.fragment_outline_cardview)).perform(click());
        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(0)
                .perform(click());
        onView(withId(R.id.activity_food_menu_ean)).perform(click());
        onView(withId(R.id.fab)).perform(click());
        Thread.sleep(2000);     // STOCKS-17
        onView(withId(R.id.fab)).perform(click());
        Thread.sleep(2000);     // STOCKS-17
        onView(withId(R.id.fab)).perform(click());
        Thread.sleep(2000);     // STOCKS-17

        StealCountAction stealCountAction = new StealCountAction();
        onView(withId(android.R.id.list)).perform(stealCountAction);
        assertEquals(3, stealCountAction.getCount());

        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(0)
                .perform(longClick());
        onView(withText("OK")).perform(click());

        onView(withId(android.R.id.list)).perform(stealCountAction);
        assertEquals(2, stealCountAction.getCount());
    }
}
