package de.njsm.stocks.linux.client.data.view;

import de.njsm.stocks.linux.client.data.Food;
import de.njsm.stocks.linux.client.data.FoodItem;

import java.util.LinkedList;
import java.util.List;

public class FoodView {

    protected final Food food;

    protected final List<FoodItem> items;

    public FoodView(Food food) {
        this.food = food;
        this.items = new LinkedList<>();
    }

    public void add(FoodItem i) {
        items.add(i);
    }

    public List<FoodItem> getItems() {
        return items;
    }

    public Food getFood() {
        return food;
    }
}
