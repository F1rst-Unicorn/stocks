package de.njsm.stocks.common.data;

import de.njsm.stocks.common.data.visitor.StocksDataVisitor;

public abstract class Data {

    public abstract <I,O> O accept(StocksDataVisitor<I,O> visitor, I input);
}
