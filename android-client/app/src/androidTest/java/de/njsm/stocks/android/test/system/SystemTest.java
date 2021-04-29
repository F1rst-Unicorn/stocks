package de.njsm.stocks.android.test.system;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.concurrent.IdlingThreadPoolExecutor;
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

        IdlingThreadPoolExecutor executor = (IdlingThreadPoolExecutor) mActivityRule.getActivity().getExecutor();
        IdlingRegistry.getInstance().register(executor);
    }

    @After
    public void tearDown() throws Exception {
        IdlingResource resource = mActivityRule.getActivity().getResource().getNestedResource();
        IdlingRegistry.getInstance().unregister(resource);
        IdlingThreadPoolExecutor executor = (IdlingThreadPoolExecutor) mActivityRule.getActivity().getExecutor();
        IdlingRegistry.getInstance().unregister(executor);
        mActivityRule.finishActivity();
    }


}
