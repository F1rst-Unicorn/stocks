package de.njsm.stocks.server.v2.business.data;

import java.util.List;
import java.util.Objects;

public class FullRecipeForInsertion implements Validatable {

    private final RecipeForInsertion recipe;

    private final List<RecipeIngredientForInsertion> ingredients;

    private final List<RecipeProductForInsertion> products;

    public FullRecipeForInsertion(RecipeForInsertion recipe, List<RecipeIngredientForInsertion> ingredients, List<RecipeProductForInsertion> products) {
        this.recipe = recipe;
        this.ingredients = ingredients;
        this.products = products;
    }

    public RecipeForInsertion getRecipe() {
        return recipe;
    }

    public List<RecipeIngredientForInsertion> getIngredients() {
        return ingredients;
    }

    public List<RecipeProductForInsertion> getProducts() {
        return products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FullRecipeForInsertion)) return false;
        FullRecipeForInsertion that = (FullRecipeForInsertion) o;
        return Objects.equals(getRecipe(), that.getRecipe());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRecipe());
    }

    @Override
    public boolean isValid() {
        return recipe != null && recipe.isValid() &&
                ingredients != null && ingredients.stream().allMatch(RecipeIngredientForInsertion::isValid) &&
                products != null && products.stream().allMatch(RecipeProductForInsertion::isValid);
    }
}
