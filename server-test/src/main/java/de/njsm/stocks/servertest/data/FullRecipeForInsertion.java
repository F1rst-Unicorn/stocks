package de.njsm.stocks.servertest.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

import java.util.List;

@AutoValue
public abstract class FullRecipeForInsertion {

    @JsonProperty
    public abstract RecipeForInsertion recipe();

    @JsonProperty
    public abstract ImmutableList<RecipeIngredientForInsertion> ingredients();

    @JsonProperty
    public abstract ImmutableList<RecipeProductForInsertion> products();

    public static Builder builder() {
        return new AutoValue_FullRecipeForInsertion.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder recipe(RecipeForInsertion v);

        public abstract Builder ingredients(List<RecipeIngredientForInsertion> v);

        public abstract ImmutableList.Builder<RecipeIngredientForInsertion> ingredientsBuilder();

        public Builder addIngredient(RecipeIngredientForInsertion v) {
            ingredientsBuilder().add(v);
            return this;
        }

        public abstract Builder products(List<RecipeProductForInsertion> v);

        public abstract ImmutableList.Builder<RecipeProductForInsertion> productsBuilder();

        public Builder addProduct(RecipeProductForInsertion v) {
            productsBuilder().add(v);
            return this;
        }

        public abstract FullRecipeForInsertion build();
    }
}
