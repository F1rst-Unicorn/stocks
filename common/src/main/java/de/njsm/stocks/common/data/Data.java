package de.njsm.stocks.common.data;

import de.njsm.stocks.common.data.visitor.StocksDataVisitor;

public abstract class Data {

    public int id;

    public abstract <I,O> O accept(StocksDataVisitor<I,O> visitor, I input);
}
