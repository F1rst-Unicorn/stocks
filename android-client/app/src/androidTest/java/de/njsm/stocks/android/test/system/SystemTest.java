package de.njsm.stocks.android.test.system;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import de.njsm.stocks.android.frontend.main.MainActivity;

public class SystemTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setup() {
        IdlingResource resource = mActivityRule.getActivity().getResource().getNestedResource();
        IdlingRegistry.getInstance().register(resource);
    }

    @After
    public void tearDown() throws Exception {
        IdlingResource resource = mActivityRule.getActivity().getResource().getNestedResource();
        IdlingRegistry.getInstance().unregister(resource);
        mActivityRule.finishActivity();
    }


}
