package de.njsm.stocks.server.v2.business.data;

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.db.jooq.tables.records.RecipeProductRecord;
import org.jooq.InsertOnDuplicateStep;
import org.jooq.InsertSetStep;

import java.util.Objects;

import static de.njsm.stocks.server.v2.db.jooq.Tables.RECIPE_PRODUCT;

public class RecipeProductWithIdForInsertion extends RecipeProductForInsertion
        implements Insertable<RecipeProductRecord, RecipeProduct> {

    private final int recipe;

    public RecipeProductWithIdForInsertion(int amount, int product, int unit, int recipe) {
        super(amount, product, unit);
        this.recipe = recipe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecipeProductWithIdForInsertion)) return false;
        if (!super.equals(o)) return false;
        RecipeProductWithIdForInsertion that = (RecipeProductWithIdForInsertion) o;
        return recipe == that.recipe;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), recipe);
    }

    @Override
    public InsertOnDuplicateStep<RecipeProductRecord> insertValue(InsertSetStep<RecipeProductRecord> arg, Principals principals) {
        return arg.columns(RECIPE_PRODUCT.AMOUNT, RECIPE_PRODUCT.PRODUCT, RECIPE_PRODUCT.RECIPE, RECIPE_PRODUCT.UNIT, RECIPE_PRODUCT.INITIATES)
                .values(getAmount(), getProduct(), recipe, getUnit(), principals.getDid());
    }

    @Override
    public boolean isContainedIn(RecipeProduct entity) {
        return getAmount() == entity.getAmount() &&
                getProduct() == entity.getProduct() &&
                recipe == entity.getRecipe() &&
                getUnit() == entity.getUnit();
    }

}
