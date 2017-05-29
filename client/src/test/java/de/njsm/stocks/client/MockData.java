package de.njsm.stocks.client;

import de.njsm.stocks.common.data.Food;
import de.njsm.stocks.common.data.view.FoodView;

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
        item.add(Utils.getDate("01.01.1970 00:00:00"));
        item.add(Utils.getDate("02.01.1970 00:00:00"));
        item.add(Utils.getDate("05.01.1970 00:00:00"));
        item.add(Utils.getDate("06.01.1970 00:00:00"));
        expectedOutput.add(item);

        item = new FoodView(new Food(2, "Carrot"));
        expectedOutput.add(item);

        item = new FoodView(new Food(3, "Bread"));
        item.add(Utils.getDate("03.01.1970 00:00:00"));
        expectedOutput.add(item);

        item = new FoodView(new Food(4, "Milk"));
        item.add(Utils.getDate("04.01.1970 00:00:00"));
        expectedOutput.add(item);

        item = new FoodView(new Food(5, "Yoghurt"));
        expectedOutput.add(item);

        item = new FoodView(new Food(6, "Raspberry jam"));
        item.add(Utils.getDate("07.01.1970 00:00:00"));
        expectedOutput.add(item);

        item = new FoodView(new Food(7, "Apple juice"));
        item.add(Utils.getDate("08.01.1970 00:00:00"));
        item.add(Utils.getDate("09.01.1970 00:00:00"));
        expectedOutput.add(item);

        return expectedOutput;
    }


}
