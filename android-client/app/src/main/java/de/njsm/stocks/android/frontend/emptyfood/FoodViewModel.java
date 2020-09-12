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

import java.util.List;

import javax.inject.Inject;

import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.views.FoodWithLatestItemView;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.repo.FoodRepository;

public class FoodViewModel extends ViewModel {

    protected FoodRepository foodRepository;

    private LiveData<Food> singleFood;

    private LiveData<List<Food>> allFood;

    protected LiveData<List<FoodWithLatestItemView>> foodByLocation;

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

    public LiveData<StatusCode> renameFood(Food item, String name) {
        return foodRepository.renameFood(item, name);
    }

    public LiveData<StatusCode> setToBuyStatus(Food item, boolean status) {
        return foodRepository.editToBuyStatus(item, status);
    }

    public void initFoodByLocation(int location) {
        if (foodByLocation == null) {
            foodByLocation = foodRepository.getFoodByLocation(location);
        }
    }

    public LiveData<List<FoodWithLatestItemView>> getCurrentFoodSubset() {
        return foodByLocation;
    }

    public LiveData<List<FoodWithLatestItemView>> getFoodByLocation() {
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

    public LiveData<StatusCode> setFoodExpirationOffset(Food food, int newOffset) {
        return foodRepository.setFoodExpirationOffset(food, newOffset);
    }

    public LiveData<StatusCode> setFoodDefaultLocation(Food food, int location) {
        return foodRepository.setFoodDefaultLocation(food, location);
    }
}
