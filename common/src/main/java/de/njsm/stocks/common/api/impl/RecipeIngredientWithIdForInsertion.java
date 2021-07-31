package de.njsm.stocks.common.api.impl;

import com.google.auto.value.AutoValue;
import de.njsm.stocks.common.api.Insertable;
import de.njsm.stocks.common.api.RecipeIngredient;
import de.njsm.stocks.common.api.RecipeIngredientForInsertionData;
import de.njsm.stocks.common.api.visitor.InsertableVisitor;

@AutoValue
public abstract class RecipeIngredientWithIdForInsertion implements Insertable<RecipeIngredient>, RecipeIngredientForInsertionData {

    public abstract int recipe();

    public static Builder builder() {
        return new AutoValue_RecipeIngredientWithIdForInsertion.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder amount(int v);

        public abstract Builder ingredient(int v);

        public abstract Builder unit(int v);

        public abstract Builder recipe(int v);

        public abstract RecipeIngredientWithIdForInsertion build();
    }

    @Override
    public boolean isContainedIn(RecipeIngredient entity) {
        return amount() == entity.getAmount() &&
                ingredient() == entity.getIngredient() &&
                recipe() == entity.getRecipe() &&
                unit() == entity.getUnit();
    }

    @Override
    public <I, O> O accept(InsertableVisitor<I, O> visitor, I argument) {
        return visitor.recipeIngredientWithIdForInsertion(this, argument);
    }
}
