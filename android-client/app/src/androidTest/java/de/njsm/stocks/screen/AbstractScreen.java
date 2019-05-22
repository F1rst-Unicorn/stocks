package de.njsm.stocks.screen;


import androidx.test.espresso.Espresso;

public class AbstractScreen {

    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }

    public AbstractScreen pressBack() {
        Espresso.pressBack();
        return this;
    }
}
