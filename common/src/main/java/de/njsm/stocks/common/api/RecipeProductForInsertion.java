package de.njsm.stocks.common.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;

@AutoValue
@JsonDeserialize(builder = AutoValue_RecipeProductForInsertion.Builder.class)
public abstract class RecipeProductForInsertion implements SelfValidating, RecipeProductForInsertionData {

    public static Builder builder() {
        return new AutoValue_RecipeProductForInsertion.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder extends SelfValidating.Builder<RecipeProductForInsertion> {
        public abstract Builder amount(int v);

        public abstract Builder product(int v);

        public abstract Builder unit(int v);
    }

    public RecipeProductWithIdForInsertion withRecipe(int recipe) {
        return RecipeProductWithIdForInsertion.builder()
                .amount(amount())
                .product(product())
                .unit(unit())
                .recipe(recipe)
                .build();
    }

    @Override
    public void validate() {
        Preconditions.checkState(product() > 0, "product id is invalid");
        Preconditions.checkState(unit() > 0, "unit id is invalid");
    }
}
