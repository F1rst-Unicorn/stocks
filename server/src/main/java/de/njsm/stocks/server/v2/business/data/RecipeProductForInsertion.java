package de.njsm.stocks.server.v2.business.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import de.njsm.stocks.server.v2.web.Endpoint;

@AutoValue
@JsonDeserialize(builder = AutoValue_RecipeProductForInsertion.class)
public abstract class RecipeProductForInsertion implements Validatable {

    public abstract int amount();

    public abstract int product();

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
