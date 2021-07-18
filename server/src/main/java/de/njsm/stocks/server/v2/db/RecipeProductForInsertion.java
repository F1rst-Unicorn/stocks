package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.data.Insertable;
import de.njsm.stocks.server.v2.business.data.RecipeProduct;
import de.njsm.stocks.server.v2.db.jooq.tables.records.RecipeProductRecord;
import org.jooq.InsertOnDuplicateStep;
import org.jooq.InsertSetStep;

import java.util.Objects;

import static de.njsm.stocks.server.v2.db.jooq.Tables.RECIPE_PRODUCT;

public class RecipeProductForInsertion implements Insertable<RecipeProductRecord, RecipeProduct> {

    private final int amount;

    private final int product;

    private final int recipe;

    private final int unit;

    public RecipeProductForInsertion(int amount, int product, int recipe, int unit) {
        this.amount = amount;
        this.product = product;
        this.recipe = recipe;
        this.unit = unit;
    }

    public int getAmount() {
        return amount;
    }

    public int getProduct() {
        return product;
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
        if (!(o instanceof RecipeProductForInsertion)) return false;
        RecipeProductForInsertion that = (RecipeProductForInsertion) o;
        return getAmount() == that.getAmount() && getProduct() == that.getProduct() && getRecipe() == that.getRecipe() && getUnit() == that.getUnit();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAmount(), getProduct(), getRecipe(), getUnit());
    }

    @Override
    public InsertOnDuplicateStep<RecipeProductRecord> insertValue(InsertSetStep<RecipeProductRecord> arg, Principals principals) {
        return arg.columns(RECIPE_PRODUCT.AMOUNT, RECIPE_PRODUCT.PRODUCT, RECIPE_PRODUCT.RECIPE, RECIPE_PRODUCT.UNIT, RECIPE_PRODUCT.INITIATES)
                .values(amount, product, recipe, unit, principals.getDid());
    }

    @Override
    public boolean isContainedIn(RecipeProduct entity) {
        return amount == entity.getAmount() &&
                product == entity.getProduct() &&
                recipe == entity.getRecipe() &&
                unit == entity.getUnit();
    }
}
