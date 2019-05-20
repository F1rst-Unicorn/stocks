package de.njsm.stocks.android.frontend.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.android.db.views.FoodView;
import de.njsm.stocks.android.repo.FoodRepository;

import javax.inject.Inject;
import java.util.List;

public class SearchViewModel extends ViewModel {

    private FoodRepository foodRepository;

    @Inject
    public SearchViewModel(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    public LiveData<List<FoodView>> search(String searchTerm) {
        return foodRepository.getFoodBySubString(searchTerm);
    }
}
