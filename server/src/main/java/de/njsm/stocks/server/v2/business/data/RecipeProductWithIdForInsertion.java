package de.njsm.stocks.server.v2.business.data;

import com.google.auto.value.AutoValue;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.db.jooq.tables.records.RecipeProductRecord;
import org.jooq.InsertOnDuplicateStep;
import org.jooq.InsertSetStep;

import static de.njsm.stocks.server.v2.db.jooq.Tables.RECIPE_PRODUCT;

@AutoValue
public abstract class RecipeProductWithIdForInsertion implements Insertable<RecipeProductRecord, RecipeProduct> {

    public abstract int amount();

    public abstract int product();

    public abstract int unit();

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
    public InsertOnDuplicateStep<RecipeProductRecord> insertValue(InsertSetStep<RecipeProductRecord> arg, Principals principals) {
        return arg.columns(RECIPE_PRODUCT.AMOUNT, RECIPE_PRODUCT.PRODUCT, RECIPE_PRODUCT.RECIPE, RECIPE_PRODUCT.UNIT, RECIPE_PRODUCT.INITIATES)
                .values(amount(), product(), recipe(), unit(), principals.getDid());
    }

    @Override
    public boolean isContainedIn(RecipeProduct entity) {
        return amount() == entity.getAmount() &&
                product() == entity.getProduct() &&
                recipe() == entity.getRecipe() &&
                unit() == entity.getUnit();
    }

}
