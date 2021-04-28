package de.njsm.stocks.android.test.system;

import de.njsm.stocks.android.Application;
import de.njsm.stocks.android.dagger.RootComponent;
import de.njsm.stocks.android.test.system.dagger.DaggerTestComponent;

public class TestApplication extends Application {

    @Override
    protected RootComponent buildDaggerComponent() {
        return DaggerTestComponent
                .builder()
                .application(this)
                .build();
    }
}
