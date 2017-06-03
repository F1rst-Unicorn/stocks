package de.njsm.stocks.client.init.upgrade;

import de.njsm.stocks.client.exceptions.InitialisationException;

public class MockUpgrader extends Upgrader {


    public MockUpgrader(Version from, Version to) {
        super(null, from, to);
    }

    @Override
    public void upgrade() throws InitialisationException {

    }
}
