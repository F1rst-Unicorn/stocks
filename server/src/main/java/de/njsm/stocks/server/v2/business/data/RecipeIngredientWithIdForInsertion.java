package de.njsm.stocks.server.v2.business.data;

import com.google.auto.value.AutoValue;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.db.jooq.tables.records.RecipeIngredientRecord;
import org.jooq.InsertOnDuplicateStep;
import org.jooq.InsertSetStep;

import static de.njsm.stocks.server.v2.db.jooq.Tables.RECIPE_INGREDIENT;

@AutoValue
public abstract class RecipeIngredientWithIdForInsertion implements Insertable<RecipeIngredientRecord, RecipeIngredient>, RecipeIngredientForInsertionData {

    public abstract int recipe();

    public static RecipeIngredientWithIdForInsertion.Builder builder() {
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

    public InsertOnDuplicateStep<RecipeIngredientRecord> insertValue(InsertSetStep<RecipeIngredientRecord> arg, Principals principals) {
        return arg.columns(RECIPE_INGREDIENT.AMOUNT, RECIPE_INGREDIENT.INGREDIENT, RECIPE_INGREDIENT.RECIPE, RECIPE_INGREDIENT.UNIT, RECIPE_INGREDIENT.INITIATES)
                .values(amount(), ingredient(), recipe(), unit(), principals.getDid());
    }

    @Override
    public boolean isContainedIn(RecipeIngredient entity) {
        return amount() == entity.getAmount() &&
                ingredient() == entity.getIngredient() &&
                recipe() == entity.getRecipe() &&
                unit() == entity.getUnit();
    }
}
