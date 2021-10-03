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

package de.njsm.stocks.android.frontend.fooditem;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.android.db.views.FoodItemView;
import de.njsm.stocks.android.db.views.ScaledAmount;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.android.repo.FoodItemRepository;
import java.time.Instant;

import java.util.List;

public class FoodItemViewModel extends ViewModel {

    private FoodItemRepository foodItemRepository;

    private LiveData<List<FoodItemView>> data;

    private LiveData<FoodItemView> item;

    public FoodItemViewModel(FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }

    public void initList(int foodId) {
        if (data == null) {
            data = foodItemRepository.getItemsOfType(foodId);
        }
    }

    public void init(int foodItemId) {
        if (item == null) {
            item = foodItemRepository.getItem(foodItemId);
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

    public LiveData<FoodItemView> getItem() {
        return item;
    }

    public LiveData<StatusCode> addItem(int foodId, int locationId, Instant eatBy, int unit) {
        return foodItemRepository.addItem(foodId, locationId, eatBy, unit);
    }

    public LiveData<Instant> getLatestExpirationOf(int foodId) {
        return foodItemRepository.getLatestExpirationOf(foodId);
    }

    public LiveData<List<ScaledAmount>> countItemsOfType(int foodId) {
        return foodItemRepository.countItemsOfType(foodId);
    }

    public LiveData<StatusCode> edit(FoodItemView item) {
        return foodItemRepository.editItem(item);
    }

    public LiveData<FoodItemView> getNowAsKnownBy(int id, Instant transactionTime) {
        return foodItemRepository.getNowAsKnownBy(id, transactionTime);
    }
}
