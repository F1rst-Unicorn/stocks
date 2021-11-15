package de.njsm.stocks.android.frontend.recipecheckout;

import androidx.lifecycle.LiveData;
import de.njsm.stocks.android.db.views.RecipeFoodCheckout;
import de.njsm.stocks.android.repo.RecipeProductRepository;
import de.njsm.stocks.android.repo.FoodItemRepository;
import de.njsm.stocks.android.repo.FoodRepository;
import de.njsm.stocks.android.repo.RecipeIngredientRepository;
import de.njsm.stocks.common.api.FoodForSetToBuy;

import java.util.List;

public class ViewModel extends androidx.lifecycle.ViewModel {

    private final RecipeIngredientRepository ingredientRepository;

    private final RecipeProductRepository productRepository;

    private final FoodRepository foodRepository;

    private final FoodItemRepository foodItemRepository;

    public ViewModel(RecipeIngredientRepository ingredientRepository, RecipeProductRepository productRepository, FoodRepository foodRepository, FoodItemRepository foodItemRepository) {
        this.ingredientRepository = ingredientRepository;
        this.productRepository = productRepository;
        this.foodRepository = foodRepository;
        this.foodItemRepository = foodItemRepository;
    }

    LiveData<List<RecipeFoodCheckout>> getIngredients(int recipeId) {
        return ingredientRepository.getIngredientsForCheckout(recipeId);
    }

    LiveData<List<RecipeFoodCheckout>> getProducts(int recipeId) {
        return productRepository.getProductsForCheckout(recipeId);
    }

    public void setFoodToBuyStatus(FoodForSetToBuy foodForSetToBuy) {
        foodRepository.editToBuyStatus(foodForSetToBuy);
    }

    public void checkoutFood(List<Adapter.FormDataItem> foodToCheckOut, List<Adapter.FormDataItem> foodToAdd) {
        foodItemRepository.checkoutFood(foodToCheckOut, foodToAdd);
    }
}
