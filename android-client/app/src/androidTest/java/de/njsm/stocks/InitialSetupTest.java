package de.njsm.stocks;

import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 19)
public class InitialSetupTest extends BaseTest {

    @Test
    public void testInitialScreen() throws Exception {
        if (! isInitialised()) {
            performRegistrationViaSentry();
        }

    }
}
