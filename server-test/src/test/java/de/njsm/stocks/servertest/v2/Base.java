package de.njsm.stocks.servertest.v2;

public class Base {

    String getUniqueName(String argument) {
        return this.getClass().getCanonicalName() + "." + argument;
    }
}
