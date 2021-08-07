package de.njsm.stocks.common.api;

import com.fasterxml.jackson.annotation.JsonGetter;

public interface RecipeIngredientForInsertionData {

    @JsonGetter
    int amount();

    @JsonGetter
    int ingredient();

    @JsonGetter
    int unit();

    interface Builder<T> {
         T amount(int v);

         T ingredient(int v);

         T unit(int v);
    }
}
