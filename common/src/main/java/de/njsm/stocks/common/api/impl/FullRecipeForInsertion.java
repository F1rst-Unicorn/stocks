package de.njsm.stocks.common.api.impl;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_FullRecipeForInsertion.Builder.class)
public abstract class FullRecipeForInsertion implements SelfValidating {

    @JsonGetter
    public abstract RecipeForInsertion recipe();

    @JsonGetter
    public abstract ImmutableList<RecipeIngredientForInsertion> ingredients();

    @JsonGetter
    public abstract ImmutableList<RecipeProductForInsertion> products();

    public static Builder builder() {
        return new AutoValue_FullRecipeForInsertion.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder extends SelfValidating.Builder<FullRecipeForInsertion> {
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
    }

    @Override
    public void validate() {
        recipe().validate();
        ingredients().forEach(RecipeIngredientForInsertion::validate);
        products().forEach(RecipeProductForInsertion::validate);
    }
}
