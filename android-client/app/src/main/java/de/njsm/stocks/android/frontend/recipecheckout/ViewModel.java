package de.njsm.stocks.android.frontend.recipecheckout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import de.njsm.stocks.android.db.views.RecipeFoodCheckout;
import de.njsm.stocks.android.repo.RecipeIngredientRepository;
import de.njsm.stocks.android.repo.RecipeRepository;

import java.util.List;

public class ViewModel extends androidx.lifecycle.ViewModel {

    private final RecipeIngredientRepository repository;

    public ViewModel(RecipeIngredientRepository repository) {
        this.repository = repository;
    }

    LiveData<List<RecipeFoodCheckout>> getIngredients(int recipeId) {
        return repository.getIngredientsForCheckout(recipeId);
    }

    LiveData<List<RecipeFoodCheckout>> getProducts() {
        return new MutableLiveData<>();
    }
}
