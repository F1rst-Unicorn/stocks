package de.njsm.stocks.android.frontend.recipecheckout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import de.njsm.stocks.android.db.views.RecipeFoodCheckout;
import de.njsm.stocks.android.repo.FoodItemRepository;
import de.njsm.stocks.android.repo.FoodRepository;
import de.njsm.stocks.android.repo.RecipeIngredientRepository;
import de.njsm.stocks.common.api.FoodForSetToBuy;

import java.util.List;

public class ViewModel extends androidx.lifecycle.ViewModel {

    private final RecipeIngredientRepository repository;

    private final FoodRepository foodRepository;

    private final FoodItemRepository foodItemRepository;

    public ViewModel(RecipeIngredientRepository repository, FoodRepository foodRepository, FoodItemRepository foodItemRepository) {
        this.repository = repository;
        this.foodRepository = foodRepository;
        this.foodItemRepository = foodItemRepository;
    }

    LiveData<List<RecipeFoodCheckout>> getIngredients(int recipeId) {
        return repository.getIngredientsForCheckout(recipeId);
    }

    LiveData<List<RecipeFoodCheckout>> getProducts() {
        return new MutableLiveData<>();
    }

    public void setFoodToBuyStatus(FoodForSetToBuy foodForSetToBuy) {
        foodRepository.editToBuyStatus(foodForSetToBuy);
    }

    public void checkoutFood(List<Adapter.FormDataItem> foodToCheckOut) {
        foodItemRepository.checkoutFood(foodToCheckOut);
    }
}
