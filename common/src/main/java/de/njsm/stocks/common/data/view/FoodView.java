package de.njsm.stocks.common.data.view;

import de.njsm.stocks.common.data.Food;
import de.njsm.stocks.common.data.FoodItem;

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
