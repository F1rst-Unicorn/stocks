/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.client;

import de.njsm.stocks.client.business.data.Food;
import de.njsm.stocks.client.business.data.view.FoodItemView;
import de.njsm.stocks.client.business.data.view.FoodView;

import java.text.ParseException;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

public class MockData {

    public static List<FoodView> getFoodViews() throws ParseException {
        return getTestFoodInDatabase();
    }

    public static List<FoodView> getTestFoodInDatabase() throws ParseException {
        List<FoodView> expectedOutput = new LinkedList<>();
        FoodView item;

        item = new FoodView(new Food(1, 6, "Beer"));
        item.add(new FoodItemView("Fridge", "Jack", "Mobile", Instant.parse("1970-01-01T00:00:00.00Z")));
        item.add(new FoodItemView("Fridge", "Jack", "Mobile", Instant.parse("1970-01-02T00:00:00.00Z")));
        item.add(new FoodItemView("Fridge", "Jack", "Mobile", Instant.parse("1970-01-05T00:00:00.00Z")));
        item.add(new FoodItemView("Fridge", "Jack", "Mobile", Instant.parse("1970-01-06T00:00:00.00Z")));
        expectedOutput.add(item);

        item = new FoodView(new Food(2, 7, "Carrot"));
        expectedOutput.add(item);

        item = new FoodView(new Food(3, 8, "Bread"));
        item.add(new FoodItemView("Cupboard", "John", "Mobile", Instant.parse("1970-01-03T00:00:00.00Z")));
        expectedOutput.add(item);

        item = new FoodView(new Food(4, 9, "Milk"));
        item.add(new FoodItemView("Fridge", "John", "Mobile", Instant.parse("1970-01-04T00:00:00.00Z")));
        expectedOutput.add(item);

        item = new FoodView(new Food(5, 10, "Yoghurt"));
        expectedOutput.add(item);

        item = new FoodView(new Food(6, 11, "Raspberry jam"));
        item.add(new FoodItemView("Cupboard", "Jack", "Mobile", Instant.parse("1970-01-07T00:00:00.00Z")));
        expectedOutput.add(item);

        item = new FoodView(new Food(7, 12, "Apple juice"));
        item.add(new FoodItemView("Cupboard", "Juliette", "Mobile", Instant.parse("1970-01-08T00:00:00.00Z")));
        item.add(new FoodItemView("Basement", "Juliette", "Mobile", Instant.parse("1970-01-09T00:00:00.00Z")));
        expectedOutput.add(item);

        return expectedOutput;
    }


}
