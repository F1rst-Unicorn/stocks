package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.data.Insertable;
import de.njsm.stocks.server.v2.business.data.RecipeIngredient;
import de.njsm.stocks.server.v2.db.jooq.tables.records.RecipeIngredientRecord;
import org.jooq.InsertOnDuplicateStep;
import org.jooq.InsertSetStep;

import java.util.Objects;

import static de.njsm.stocks.server.v2.db.jooq.tables.RecipeIngredient.RECIPE_INGREDIENT;

public class RecipeIngredientForInsertion implements Insertable<RecipeIngredientRecord, RecipeIngredient> {

    private final int amount;

    private final int ingredient;

    private final int recipe;

    private final int unit;

    public RecipeIngredientForInsertion(int amount, int ingredient, int recipe, int unit) {
        this.amount = amount;
        this.ingredient = ingredient;
        this.recipe = recipe;
        this.unit = unit;
    }

    public int getAmount() {
        return amount;
    }

    public int getIngredient() {
        return ingredient;
    }

    public int getRecipe() {
        return recipe;
    }

    public int getUnit() {
        return unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecipeIngredientForInsertion)) return false;
        RecipeIngredientForInsertion that = (RecipeIngredientForInsertion) o;
        return getAmount() == that.getAmount() && getIngredient() == that.getIngredient() && getRecipe() == that.getRecipe() && getUnit() == that.getUnit();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAmount(), getIngredient(), getRecipe(), getUnit());
    }

    @Override
    public InsertOnDuplicateStep<RecipeIngredientRecord> insertValue(InsertSetStep<RecipeIngredientRecord> arg, Principals principals) {
        return arg.columns(RECIPE_INGREDIENT.AMOUNT, RECIPE_INGREDIENT.INGREDIENT, RECIPE_INGREDIENT.RECIPE, RECIPE_INGREDIENT.UNIT, RECIPE_INGREDIENT.INITIATES)
                .values(amount, ingredient, recipe, unit, principals.getDid());
    }

    @Override
    public boolean isContainedIn(RecipeIngredient entity) {
        return amount == entity.getAmount() &&
                ingredient == entity.getIngredient() &&
                recipe == entity.getRecipe() &&
                unit == entity.getUnit();
    }
}
