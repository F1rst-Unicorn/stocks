package de.njsm.stocks.android.frontend.eatsoon;

import androidx.lifecycle.LiveData;
import de.njsm.stocks.android.db.views.FoodView;
import de.njsm.stocks.android.frontend.emptyfood.FoodViewModel;
import de.njsm.stocks.android.repo.FoodRepository;

import java.util.List;

public class FoodToEatViewModel extends FoodViewModel {

    private LiveData<List<FoodView>> data;

    public FoodToEatViewModel(FoodRepository foodRepository) {
        super(foodRepository);
    }

    public void init() {
        if (data == null) {
            data = foodRepository.getFoodToEat();
        }
    }

    public LiveData<List<FoodView>> getFoodToEat() {
        return data;
    }
}
