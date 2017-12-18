package de.njsm.stocks;

import android.support.test.rule.ActivityTestRule;
import de.njsm.stocks.frontend.StartupActivity;
import de.njsm.stocks.screen.MainScreen;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

@RunWith(Parameterized.class)
public class LocationAddTest {

    @Rule
    public ActivityTestRule<StartupActivity> mActivityRule = new ActivityTestRule<>(StartupActivity.class);

    @Parameterized.Parameter
    public String locationName;

    @Parameterized.Parameters(name = "Location {0}")
    public static Iterable<Object[]> getFoodNames() {
        return Arrays.asList(
                new Object[][] {
                        {"Fridge"},
                        {"Ground"},
                        {"Cupboard"},
                        {"Basement"}
                });
    }

    @Test
    public void addLocation() throws Exception {
        MainScreen.test()
                .goToLocations()
                .addLocation(locationName)
                .assertLastItemIsNamed(locationName);
    }
}
