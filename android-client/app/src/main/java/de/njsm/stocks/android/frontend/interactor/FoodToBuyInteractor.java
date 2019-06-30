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

package de.njsm.stocks.android.frontend.interactor;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.network.server.StatusCode;

import java.util.function.BiFunction;

public class FoodToBuyInteractor extends EditInteractor<Food, Boolean> {

    private Function<Integer, LiveData<Food>> updater;

    public FoodToBuyInteractor(BaseFragment owner,
                               BiFunction<Food, Boolean, LiveData<StatusCode>> editor,
                               Function<Integer, LiveData<Food>> updater) {
        super(owner, editor);
        this.updater = updater;
    }

    @Override
    protected void treatErrorCode(StatusCode code, Food item, Boolean editedData) {
        if (code == StatusCode.INVALID_DATA_VERSION) {
            LiveData<Food> newData = updater.apply(item.id);
            newData.observe(owner, newItem -> {
                if (newItem != null && !newItem.equals(item) && item.toBuy != newItem.toBuy) {
                    observeEditing(newItem, editedData);
                    newData.removeObservers(owner);
                }
            });
        } else
            owner.maybeShowEditError(code);
    }
}
