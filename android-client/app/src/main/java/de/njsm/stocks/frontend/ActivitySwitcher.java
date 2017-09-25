package de.njsm.stocks.frontend;

import android.content.Context;
import android.content.Intent;
import de.njsm.stocks.common.data.Food;
import de.njsm.stocks.frontend.eatsoon.EatSoonActivity;
import de.njsm.stocks.frontend.emptyfood.EmptyFoodActivity;
import de.njsm.stocks.frontend.food.FoodActivity;
import de.njsm.stocks.frontend.settings.SettingsActivity;

public class ActivitySwitcher {

    public static void switchToFoodActivity(Context context, Food food) {
        Intent i = new Intent(context, FoodActivity.class);
        i.putExtra(FoodActivity.KEY_ID, food.id);
        i.putExtra(FoodActivity.KEY_NAME, food.name);
        context.startActivity(i);
    }

    public static void switchToSettings(Context context) {
        Intent i = new Intent(context, SettingsActivity.class);
        context.startActivity(i);
    }

    public static void switchToEatSoonActivity(Context context) {
        Intent i = new Intent(context, EatSoonActivity.class);
        context.startActivity(i);
    }

    public static void switchToEmptyFoodActivity(Context context) {
        Intent i = new Intent(context, EmptyFoodActivity.class);
        context.startActivity(i);
    }
}
