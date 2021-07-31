package de.njsm.stocks.common.api.impl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;
import de.njsm.stocks.common.api.RecipeIngredientForInsertionData;

@AutoValue
@JsonDeserialize(builder = AutoValue_RecipeIngredientForInsertion.Builder.class)
public abstract class RecipeIngredientForInsertion implements SelfValidating, RecipeIngredientForInsertionData {

    public static Builder builder() {
        return new AutoValue_RecipeIngredientForInsertion.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder extends SelfValidating.Builder<RecipeIngredientForInsertion> {
        public abstract Builder amount(int v);

        public abstract Builder ingredient(int v);

        public abstract Builder unit(int v);
    }

    @Override
    public void validate() {
        Preconditions.checkState(ingredient() > 0, "ingredient id is invalid");
        Preconditions.checkState(unit() > 0, "unit id is invalid");
    }

    public RecipeIngredientWithIdForInsertion withRecipe(int recipe) {
        return RecipeIngredientWithIdForInsertion.builder()
                .amount(amount())
                .ingredient(ingredient())
                .unit(unit())
                .recipe(recipe)
                .build();
    }
}
