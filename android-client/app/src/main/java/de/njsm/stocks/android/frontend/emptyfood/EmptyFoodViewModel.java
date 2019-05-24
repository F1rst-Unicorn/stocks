package de.njsm.stocks.android.frontend.emptyfood;

import androidx.lifecycle.LiveData;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.repo.FoodRepository;
import de.njsm.stocks.android.util.Logger;

import javax.inject.Inject;
import java.util.List;

public class EmptyFoodViewModel extends FoodViewModel {

    private static final Logger LOG = new Logger(EmptyFoodViewModel.class);

    private LiveData<List<Food>> food;

    @Inject
    public EmptyFoodViewModel(FoodRepository foodRepository) {
        super(foodRepository);
    }

    public void init() {
        if (food == null) {
            LOG.d("Initialising");
            food = foodRepository.getEmptyFood();
        }
    }

    LiveData<List<Food>> getEmptyFood() {
        return food;
    }

}
