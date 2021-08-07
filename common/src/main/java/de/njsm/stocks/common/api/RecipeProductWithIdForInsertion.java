package de.njsm.stocks.common.api;

import com.google.auto.value.AutoValue;
import de.njsm.stocks.common.api.visitor.InsertableVisitor;

@AutoValue
public abstract class RecipeProductWithIdForInsertion implements Insertable<RecipeProduct>, RecipeProductForInsertionData {

    public abstract int recipe();

    public static Builder builder() {
        return new AutoValue_RecipeProductWithIdForInsertion.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder amount(int v);

        public abstract Builder product(int v);

        public abstract Builder unit(int v);

        public abstract Builder recipe(int v);

        public abstract RecipeProductWithIdForInsertion build();
    }

    @Override
    public boolean isContainedIn(RecipeProduct entity) {
        return amount() == entity.amount() &&
                product() == entity.product() &&
                recipe() == entity.recipe() &&
                unit() == entity.unit();
    }

    @Override
    public <I, O> O accept(InsertableVisitor<I, O> visitor, I argument) {
        return visitor.recipeProductWithIdForInsertion(this, argument);
    }
}
