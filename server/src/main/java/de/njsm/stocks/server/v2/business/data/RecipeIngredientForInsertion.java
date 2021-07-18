package de.njsm.stocks.server.v2.business.data;

import de.njsm.stocks.server.v2.web.Endpoint;

import java.util.Objects;

public class RecipeIngredientForInsertion
        implements Validatable {

    private final int amount;

    private final int ingredient;

    private final int unit;

    public RecipeIngredientForInsertion(int amount, int ingredient, int unit) {
        this.amount = amount;
        this.ingredient = ingredient;
        this.unit = unit;
    }

    public int getAmount() {
        return amount;
    }

    public int getIngredient() {
        return ingredient;
    }

    public int getUnit() {
        return unit;
    }

    public RecipeIngredientWithIdForInsertion withRecipe(int recipe) {
        return new RecipeIngredientWithIdForInsertion(amount, ingredient, unit, recipe);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecipeIngredientForInsertion)) return false;
        RecipeIngredientForInsertion that = (RecipeIngredientForInsertion) o;
        return getAmount() == that.getAmount() && getIngredient() == that.getIngredient() && getUnit() == that.getUnit();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAmount(), getIngredient(), getUnit());
    }

    @Override
    public boolean isValid() {
        return Endpoint.isValid(ingredient, "ingredient") &&
                Endpoint.isValid(unit, "unit");
    }
}
