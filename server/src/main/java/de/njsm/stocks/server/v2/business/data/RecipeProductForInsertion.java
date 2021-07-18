package de.njsm.stocks.server.v2.business.data;

import de.njsm.stocks.server.v2.web.Endpoint;

import java.util.Objects;

public class RecipeProductForInsertion
        implements Validatable {

    private final int amount;

    private final int product;

    private final int unit;

    public RecipeProductForInsertion(int amount, int product, int unit) {
        this.amount = amount;
        this.product = product;
        this.unit = unit;
    }

    public int getAmount() {
        return amount;
    }

    public int getProduct() {
        return product;
    }

    public int getUnit() {
        return unit;
    }

    public RecipeProductWithIdForInsertion withRecipe(int recipe) {
        return new RecipeProductWithIdForInsertion(amount, product, unit, recipe);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecipeProductForInsertion)) return false;
        RecipeProductForInsertion that = (RecipeProductForInsertion) o;
        return getAmount() == that.getAmount() && getProduct() == that.getProduct() && getUnit() == that.getUnit();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAmount(), getProduct(), getUnit());
    }

    @Override
    public boolean isValid() {
        return Endpoint.isValid(product, "product") &&
                Endpoint.isValid(unit, "unit");
    }
}
