package de.njsm.stocks.server.v2.business.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import de.njsm.stocks.server.v2.web.Endpoint;

@AutoValue
@JsonDeserialize(builder = AutoValue_RecipeIngredientForInsertion.Builder.class)
public abstract class RecipeIngredientForInsertion implements Validatable, RecipeIngredientForInsertionData {

    public static Builder builder() {
        return new AutoValue_RecipeIngredientForInsertion.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder {
        public abstract Builder amount(int v);

        public abstract Builder ingredient(int v);

        public abstract Builder unit(int v);

        public abstract RecipeIngredientForInsertion build();
    }

    public RecipeIngredientWithIdForInsertion withRecipe(int recipe) {
        return RecipeIngredientWithIdForInsertion.builder()
                .amount(amount())
                .ingredient(ingredient())
                .unit(unit())
                .recipe(recipe)
                .build();
    }

    @Override
    public boolean isValid() {
        return Endpoint.isValid(ingredient(), "ingredient") &&
                Endpoint.isValid(unit(), "unit");
    }
}
