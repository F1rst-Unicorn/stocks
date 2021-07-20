package de.njsm.stocks.servertest.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize(builder = AutoValue_RecipeProductForInsertion.Builder.class)
public abstract class RecipeProductForInsertion {

    @JsonProperty
    public abstract int amount();

    @JsonProperty
    public abstract int product();

    @JsonProperty
    public abstract int unit();

    public static Builder builder() {
        return new AutoValue_RecipeProductForInsertion.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder amount(int v);

        public abstract Builder product(int v);

        public abstract Builder unit(int v);

        public abstract RecipeProductForInsertion build();
    }
}
