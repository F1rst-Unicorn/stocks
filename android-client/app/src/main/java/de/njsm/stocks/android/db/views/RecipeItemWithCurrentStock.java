/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2021  The stocks developers
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

package de.njsm.stocks.android.db.views;

import androidx.arch.core.util.Function;
import androidx.room.Embedded;
import de.njsm.stocks.android.db.util.Aggregator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RecipeItemWithCurrentStock {

    private final ScaledFood scaledFood;

    private final List<ScaledAmount> currentStock;

    public RecipeItemWithCurrentStock(ScaledFood scaledFood) {
        this.scaledFood = scaledFood;
        this.currentStock = new ArrayList<>();
    }

    public String format(String formatString) {
        return String.format(formatString, printIngredientAmount(), printCurrentStock());
    }

    private String printIngredientAmount() {
        return getScaledFood().getPrettyString();
    }

    public RecipeItemWithCurrentStock merge(SingleRecipeItemWithCurrentStock single) {
        getCurrentStock().add(single.currentStock);
        return this;
    }

    private String printCurrentStock() {
        return ScaledAmount.getPrettyString(getCurrentStock());
    }

    private List<ScaledAmount> getCurrentStock() {
        return currentStock;
    }

    private ScaledFood getScaledFood() {
        return scaledFood;
    }

    public static class SingleRecipeItemWithCurrentStock {

        @Embedded
        private final ScaledFood scaledFood;

        @Embedded(prefix = de.njsm.stocks.android.db.dbview.ScaledAmount.SCALED_AMOUNT_PREFIX)
        private final ScaledAmount currentStock;

        public SingleRecipeItemWithCurrentStock(ScaledFood scaledFood, ScaledAmount currentStock) {
            this.scaledFood = scaledFood;
            this.currentStock = currentStock;
        }

        public RecipeItemWithCurrentStock into() {
            RecipeItemWithCurrentStock result = new RecipeItemWithCurrentStock(scaledFood);
            result.getCurrentStock().add(currentStock);
            return result;
        }
    }

    public static class Mapper implements Function<List<SingleRecipeItemWithCurrentStock>, List<RecipeItemWithCurrentStock>> {

        @Override
        public List<RecipeItemWithCurrentStock> apply(List<SingleRecipeItemWithCurrentStock> input) {
            return StreamSupport.stream(new Spliterator(input.iterator()), false)
                    .collect(Collectors.toList());
        }
    }

    public static class Spliterator extends Aggregator<SingleRecipeItemWithCurrentStock, RecipeItemWithCurrentStock> {

        public Spliterator(Iterator<SingleRecipeItemWithCurrentStock> iterator) {
            super(iterator);
        }

        @Override
        public RecipeItemWithCurrentStock base(SingleRecipeItemWithCurrentStock input) {
            return input.into();
        }

        @Override
        public boolean sameGroup(RecipeItemWithCurrentStock current, SingleRecipeItemWithCurrentStock input) {
            return current.getScaledFood().getFood().getId() == input.scaledFood.getFood().getId();
        }

        @Override
        public RecipeItemWithCurrentStock merge(RecipeItemWithCurrentStock current, SingleRecipeItemWithCurrentStock input) {
            return current.merge(input);
        }
    }
}
