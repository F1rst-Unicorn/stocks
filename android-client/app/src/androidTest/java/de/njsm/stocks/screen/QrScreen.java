package de.njsm.stocks.screen;


import static androidx.test.espresso.Espresso.pressBack;

public class QrScreen {

    public PrincipalsScreen next() {
        pressBack();
        return new PrincipalsScreen();
    }
}
