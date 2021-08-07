package de.njsm.stocks.common.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;

@AutoValue
@JsonDeserialize(builder = AutoValue_RecipeIngredientForInsertion.Builder.class)
public abstract class RecipeIngredientForInsertion implements SelfValidating, RecipeIngredientForInsertionData {

    public static Builder builder() {
        return new AutoValue_RecipeIngredientForInsertion.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder
            extends SelfValidating.Builder<RecipeIngredientForInsertion>
            implements RecipeIngredientForInsertionData.Builder<Builder> {
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
