package de.njsm.stocks.android.frontend.fooditem;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.android.db.views.FoodItemView;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.repo.FoodItemRepository;

import java.util.List;

public class FoodItemViewModel extends ViewModel {

    private FoodItemRepository foodItemRepository;

    private LiveData<List<FoodItemView>> data;

    public FoodItemViewModel(FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }

    public void init(int foodId) {
        if (data == null) {
            data = foodItemRepository.getItemsOfType(foodId);
        }
    }

    public LiveData<List<FoodItemView>> getFoodItems() {
        return data;
    }

    public LiveData<StatusCode> deleteItem(FoodItemView t) {
        return foodItemRepository.deleteItem(t);
    }

    public LiveData<FoodItemView> getItem(int id) {
        return foodItemRepository.getItem(id);
    }
}
