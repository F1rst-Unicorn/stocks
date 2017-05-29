package de.njsm.stocks.client.network.server;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.visitor.StocksDataVisitor;

import java.util.List;

public class DataConverter {

    private List<StocksDataVisitor<Void, Void>> converters;

    public DataConverter(List<StocksDataVisitor<Void, Void>> converters) {
        this.converters = converters;
    }

    public void convert(Data item) {
        for (StocksDataVisitor<Void, Void> visitor : converters) {
            visitor.visit(item, null);
        }
    }

    public void convert(Data[] items) {
        for (Data item : items) {
            convert(item);
        }
    }

}
