package de.njsm.stocks.servertest.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize(builder = AutoValue_RecipeIngredientForInsertion.Builder.class)
public abstract class RecipeIngredientForInsertion {

    @JsonProperty
    public abstract int amount();

    @JsonProperty
    public abstract int ingredient();

    @JsonProperty
    public abstract int unit();

    public static Builder builder() {
        return new AutoValue_RecipeIngredientForInsertion.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder amount(int v);

        public abstract Builder ingredient(int v);

        public abstract Builder unit(int v);

        public abstract RecipeIngredientForInsertion build();
    }
}
