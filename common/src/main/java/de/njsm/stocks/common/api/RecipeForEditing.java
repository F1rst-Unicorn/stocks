package de.njsm.stocks.common.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize(builder = AutoValue_RecipeForEditing.Builder.class)
public abstract class RecipeForEditing implements Recipe {

    public static Builder builder() {
        return new AutoValue_RecipeForEditing.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder
            extends SelfValidating.Builder<RecipeForEditing>
            implements Versionable.Builder<Builder>, Recipe.Builder<Builder> {
    }
}
