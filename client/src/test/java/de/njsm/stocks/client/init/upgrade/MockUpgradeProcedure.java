package de.njsm.stocks.client.init.upgrade;

import de.njsm.stocks.client.exceptions.InitialisationException;

public class MockUpgradeProcedure extends UpgradeProcedure {


    public MockUpgradeProcedure(Version from, Version to) {
        super(null, from, to);
    }

    @Override
    public void upgrade() throws InitialisationException {

    }

    @Override
    public void downgrade() throws InitialisationException {

    }
}
