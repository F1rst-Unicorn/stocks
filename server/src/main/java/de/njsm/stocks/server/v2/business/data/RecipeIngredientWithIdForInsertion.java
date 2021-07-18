package de.njsm.stocks.server.v2.business.data;

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.db.jooq.tables.records.RecipeIngredientRecord;
import org.jooq.InsertOnDuplicateStep;
import org.jooq.InsertSetStep;

import java.util.Objects;

import static de.njsm.stocks.server.v2.db.jooq.Tables.RECIPE_INGREDIENT;

public class RecipeIngredientWithIdForInsertion extends RecipeIngredientForInsertion
        implements Insertable<RecipeIngredientRecord, RecipeIngredient> {

    private final int recipe;

    public RecipeIngredientWithIdForInsertion(int amount, int ingredient, int unit, int recipe) {
        super(amount, ingredient, unit);
        this.recipe = recipe;
    }

    public int getRecipe() {
        return recipe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecipeIngredientWithIdForInsertion)) return false;
        if (!super.equals(o)) return false;
        RecipeIngredientWithIdForInsertion that = (RecipeIngredientWithIdForInsertion) o;
        return getRecipe() == that.getRecipe();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getRecipe());
    }

    public InsertOnDuplicateStep<RecipeIngredientRecord> insertValue(InsertSetStep<RecipeIngredientRecord> arg, Principals principals) {
        return arg.columns(RECIPE_INGREDIENT.AMOUNT, RECIPE_INGREDIENT.INGREDIENT, RECIPE_INGREDIENT.RECIPE, RECIPE_INGREDIENT.UNIT, RECIPE_INGREDIENT.INITIATES)
                .values(getAmount(), getIngredient(), recipe, getUnit(), principals.getDid());
    }

    @Override
    public boolean isContainedIn(RecipeIngredient entity) {
        return getAmount() == entity.getAmount() &&
                getIngredient() == entity.getIngredient() &&
                getRecipe() == entity.getRecipe() &&
                getUnit() == entity.getUnit();
    }
}
