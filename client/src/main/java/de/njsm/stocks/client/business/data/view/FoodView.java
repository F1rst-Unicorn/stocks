package de.njsm.stocks.client.business.data.view;


import de.njsm.stocks.client.business.data.Food;

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
