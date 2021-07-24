package de.njsm.stocks.server.v2.business.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_FullRecipeForInsertion.Builder.class)
public abstract class FullRecipeForInsertion implements Validatable {

    public abstract RecipeForInsertion recipe();

    public abstract ImmutableList<RecipeIngredientForInsertion> ingredients();

    public abstract ImmutableList<RecipeProductForInsertion> products();

    public static Builder builder() {
        return new AutoValue_FullRecipeForInsertion.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder {
        public abstract Builder recipe(RecipeForInsertion v);

        public abstract Builder ingredients(List<RecipeIngredientForInsertion> v);

        public abstract Builder products(List<RecipeProductForInsertion> v);

        public abstract FullRecipeForInsertion build();
    }

    @Override
    public boolean isValid() {
        return recipe().isValid() &&
                ingredients().stream().allMatch(RecipeIngredientForInsertion::isValid) &&
                products().stream().allMatch(RecipeProductForInsertion::isValid);
    }
}
