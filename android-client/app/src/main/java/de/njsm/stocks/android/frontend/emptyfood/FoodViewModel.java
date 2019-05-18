package de.njsm.stocks.android.frontend.emptyfood;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.views.FoodView;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.repo.FoodRepository;

import javax.inject.Inject;
import java.util.List;

public class FoodViewModel extends ViewModel {

    protected FoodRepository foodRepository;

    @Inject
    public FoodViewModel(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    public LiveData<StatusCode> addFood(String name) {
        return foodRepository.addFood(name);
    }

    public LiveData<StatusCode> deleteFood(Food item) {
        return foodRepository.deleteFood(item);
    }

    public LiveData<Food> getFood(int id) {
        return foodRepository.getFood(id);
    }

    public LiveData<StatusCode> renameFood(Food item, String name) {
        return foodRepository.renameFood(item, name);
    }

    public LiveData<List<FoodView>> getFoodByLocation(int location) {
        return foodRepository.getFoodByLocation(location);
    }
}
