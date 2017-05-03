package de.njsm.stocks.common.data.visitor;

import de.njsm.stocks.common.data.FoodItem;
import de.njsm.stocks.common.data.Update;

import java.util.Date;
import java.util.TimeZone;

public class DateToLocaltimeConverter extends StocksDataVisitorImpl<Void, Void> {

    @Override
    public Void foodItem(FoodItem item, Void input) {
        item.eatByDate = convert(item.eatByDate);
        return null;
    }

    @Override
    public Void update(Update update, Void input) {
        update.lastUpdate = convert(update.lastUpdate);
        return null;
    }

    private Date convert(Date utcDate) {
        return new Date(utcDate.getTime()
                - TimeZone.getDefault().getOffset(utcDate.getTime()));
    }
}
