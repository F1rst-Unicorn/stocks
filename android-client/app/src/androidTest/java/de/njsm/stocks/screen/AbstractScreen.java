package de.njsm.stocks.screen;

import android.support.test.espresso.Espresso;

public class AbstractScreen {

    public AbstractScreen pressBack() {
        Espresso.pressBack();
        return this;
    }
}
