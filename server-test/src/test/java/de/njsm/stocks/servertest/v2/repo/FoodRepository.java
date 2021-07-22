package de.njsm.stocks.servertest.v2.repo;

import de.njsm.stocks.servertest.v2.FoodTest;

import java.util.List;

public class FoodRepository {

    public static int getAnyFoodId() {
        List<Integer> ids = FoodTest.assertOnFood(false)
                        .extract()
                        .jsonPath()
                        .getList("data.id");

        if (ids.isEmpty())
            return FoodTest.createNewFoodType("getAnyFoodId");
        else
            return ids.get(0);
    }
}
