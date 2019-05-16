package de.njsm.stocks.android.frontend.emptyfood;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.repo.FoodRepository;

import javax.inject.Inject;

public class FoodViewModel extends ViewModel {

    protected FoodRepository foodRepository;

    @Inject
    public FoodViewModel(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    public LiveData<StatusCode> addFood(String name) {
        return foodRepository.addFood(name);
    }

    protected LiveData<StatusCode> deleteFood(Food item) {
        return foodRepository.deleteFood(item);
    }

    protected LiveData<Food> getFood(int id) {
        return foodRepository.getFood(id);
    }
}
