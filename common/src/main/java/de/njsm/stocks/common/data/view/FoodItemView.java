package de.njsm.stocks.common.data.view;

import org.threeten.bp.Instant;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoodItemView that = (FoodItemView) o;
        return Objects.equals(location, that.location) &&
                Objects.equals(user, that.user) &&
                Objects.equals(device, that.device) &&
                Objects.equals(eatByDate, that.eatByDate);
    }

    @Override
    public int hashCode() {

        return Objects.hash(location, user, device, eatByDate);
    }

    @Override
    public String toString() {
        return "FoodItemView{" +
                "location=" + location +
                ", user=" + user +
                ", device=" + device +
                ", eatBy=" + eatByDate.toEpochMilli() + "}";
    }
}
