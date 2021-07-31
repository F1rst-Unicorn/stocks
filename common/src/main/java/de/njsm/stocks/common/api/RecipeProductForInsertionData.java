package de.njsm.stocks.common.api;

import com.fasterxml.jackson.annotation.JsonGetter;

public interface RecipeProductForInsertionData {

    @JsonGetter
    int amount();

    @JsonGetter
    int product();

    @JsonGetter
    int unit();
}
