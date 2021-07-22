package de.njsm.stocks.server.v2.business.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import de.njsm.stocks.server.v2.web.Endpoint;

@AutoValue
@JsonDeserialize(builder = AutoValue_RecipeProductForInsertion.Builder.class)
public abstract class RecipeProductForInsertion implements Validatable, RecipeProductForInsertionData {

    public static Builder builder() {
        return new AutoValue_RecipeProductForInsertion.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder {
        public abstract Builder amount(int v);

        public abstract Builder product(int v);

        public abstract Builder unit(int v);

        public abstract RecipeProductForInsertion build();
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
    public boolean isValid() {
        return Endpoint.isValid(product(), "product") &&
                Endpoint.isValid(unit(), "unit");
    }
}
