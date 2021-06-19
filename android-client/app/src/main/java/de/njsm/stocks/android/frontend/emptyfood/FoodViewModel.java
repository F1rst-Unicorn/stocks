/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.stocks.android.frontend.emptyfood;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.views.FoodSummaryView;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.repo.FoodRepository;
import org.threeten.bp.Instant;

import javax.inject.Inject;
import java.util.List;

public class FoodViewModel extends ViewModel {

    protected FoodRepository foodRepository;

    private LiveData<Food> singleFood;

    private LiveData<List<Food>> allFood;

    protected LiveData<List<FoodSummaryView>> foodByLocation;

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

    public void initFood(int foodId) {
        singleFood = foodRepository.getFood(foodId);
    }

    public LiveData<Food> getFood() {
        return singleFood;
    }

    public LiveData<Food> getFood(int id) {
        return foodRepository.getFood(id);
    }

    public LiveData<StatusCode> edit(Food item) {
        return foodRepository.editFood(item);
    }

    public LiveData<StatusCode> renameFood(Food item, String newName) {
        Food editedFood = item.copy();
        editedFood.name = newName;
        return foodRepository.editFood(editedFood);
    }

    public LiveData<StatusCode> setToBuyStatus(Food item, boolean status) {
        return foodRepository.editToBuyStatus(item, status);
    }

    public void initFoodByLocation(int location) {
        if (foodByLocation == null) {
            foodByLocation = foodRepository.getFoodByLocationSummary(location);
        }
    }

    public LiveData<List<FoodSummaryView>> getCurrentFoodSubset() {
        return foodByLocation;
    }

    public LiveData<List<FoodSummaryView>> getFoodByLocation() {
        return foodByLocation;
    }

    public void initAllFood() {
        if (allFood == null) {
            allFood = foodRepository.getFood();
        }
    }

    public LiveData<List<Food>> getAllFood() {
        return allFood;
    }

    public LiveData<Food> getFoodByEanNumber(String s) {
        return foodRepository.getFoodByEanNumber(s);
    }

    public LiveData<Food> getFoodNowAsKnownBy(int id, Instant transactionTimeStart) {
        return foodRepository.getFoodNowAsKnownBy(id, transactionTimeStart);
    }
}
