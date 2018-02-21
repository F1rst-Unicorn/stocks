package de.njsm.stocks.client;

import de.njsm.stocks.common.data.Food;
import de.njsm.stocks.common.data.view.FoodItemView;
import de.njsm.stocks.common.data.view.FoodView;
import org.threeten.bp.Instant;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

public class MockData {

    public static List<FoodView> getFoodViews() throws ParseException {
        return getTestFoodInDatabase();
    }

    public static List<FoodView> getTestFoodInDatabase() throws ParseException {
        List<FoodView> expectedOutput = new LinkedList<>();
        FoodView item;

        item = new FoodView(new Food(1, "Beer"));
        item.add(new FoodItemView("Fridge", "Jack", "Mobile", Instant.parse("1970-01-01T00:00:00.00Z")));
        item.add(new FoodItemView("Fridge", "Jack", "Mobile", Instant.parse("1970-01-02T00:00:00.00Z")));
        item.add(new FoodItemView("Fridge", "Jack", "Mobile", Instant.parse("1970-01-05T00:00:00.00Z")));
        item.add(new FoodItemView("Fridge", "Jack", "Mobile", Instant.parse("1970-01-06T00:00:00.00Z")));
        expectedOutput.add(item);

        item = new FoodView(new Food(2, "Carrot"));
        expectedOutput.add(item);

        item = new FoodView(new Food(3, "Bread"));
        item.add(new FoodItemView("Cupboard", "John", "Mobile", Instant.parse("1970-01-03T00:00:00.00Z")));
        expectedOutput.add(item);

        item = new FoodView(new Food(4, "Milk"));
        item.add(new FoodItemView("Fridge", "John", "Mobile", Instant.parse("1970-01-04T00:00:00.00Z")));
        expectedOutput.add(item);

        item = new FoodView(new Food(5, "Yoghurt"));
        expectedOutput.add(item);

        item = new FoodView(new Food(6, "Raspberry jam"));
        item.add(new FoodItemView("Cupboard", "Jack", "Mobile", Instant.parse("1970-01-07T00:00:00.00Z")));
        expectedOutput.add(item);

        item = new FoodView(new Food(7, "Apple juice"));
        item.add(new FoodItemView("Cupboard", "Juliette", "Mobile", Instant.parse("1970-01-08T00:00:00.00Z")));
        item.add(new FoodItemView("Basement", "Juliette", "Mobile", Instant.parse("1970-01-09T00:00:00.00Z")));
        expectedOutput.add(item);

        return expectedOutput;
    }


}
