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


import de.njsm.stocks.clientold.business.data.Food;

import java.util.LinkedList;
import java.util.List;

public class FoodView {

    protected final Food food;

    private final List<FoodItemView> items;

    public FoodView(Food food) {
        this.food = food;
        this.items = new LinkedList<>();
    }

    public void add(FoodItemView i) {
        items.add(i);
    }

    public List<FoodItemView> getItems() {
        return items;
    }

    public Food getFood() {
        return food;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FoodView foodView = (FoodView) o;

        if (!food.equals(foodView.food)) return false;
        return items.equals(foodView.items);
    }

    @Override
    public int hashCode() {
        int result = food.hashCode();
        result = 31 * result + items.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FoodView{" +
                "food=" + food +
                ", items=" + items +
                '}';
    }
}
