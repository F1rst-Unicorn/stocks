package de.njsm.stocks.server.v1.internal.data;

import de.njsm.stocks.server.v1.internal.data.visitor.StocksDataVisitor;

public abstract class Data {

    public int id;

    public abstract <I,O> O accept(StocksDataVisitor<I,O> visitor, I input);
}
