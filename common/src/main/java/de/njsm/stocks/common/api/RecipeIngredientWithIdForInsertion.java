package de.njsm.stocks.common.api;

import com.google.auto.value.AutoValue;
import de.njsm.stocks.common.api.visitor.InsertableVisitor;

@AutoValue
public abstract class RecipeIngredientWithIdForInsertion implements Insertable<RecipeIngredient>, RecipeIngredientForInsertionData {

    public abstract int recipe();

    public static Builder builder() {
        return new AutoValue_RecipeIngredientWithIdForInsertion.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder
            implements RecipeIngredientForInsertionData.Builder<Builder> {

        public abstract Builder recipe(int v);

        public abstract RecipeIngredientWithIdForInsertion build();
    }

    @Override
    public boolean isContainedIn(RecipeIngredient entity) {
        return amount() == entity.amount() &&
                ingredient() == entity.ingredient() &&
                recipe() == entity.recipe() &&
                unit() == entity.unit();
    }

    @Override
    public <I, O> O accept(InsertableVisitor<I, O> visitor, I argument) {
        return visitor.recipeIngredientWithIdForInsertion(this, argument);
    }
}
