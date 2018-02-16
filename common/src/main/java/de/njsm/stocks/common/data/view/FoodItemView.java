package de.njsm.stocks.common.data.view;

import org.threeten.bp.Instant;

public class FoodItemView {

    public String location;

    public String user;

    public String device;

    public Instant eatByDate;

    public FoodItemView() {
    }

    public FoodItemView(String location, String user, String device, Instant eatByDate) {
        this.location = location;
        this.user = user;
        this.device = device;
        this.eatByDate = eatByDate;
    }
}
