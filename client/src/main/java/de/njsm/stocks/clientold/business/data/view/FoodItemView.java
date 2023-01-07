/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.njsm.stocks.clientold.business.data.view;


import java.time.Instant;
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
