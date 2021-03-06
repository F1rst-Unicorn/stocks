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
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.network.server.StatusCode;

import java.util.function.BiFunction;

public class FoodEditInteractor extends EditInteractor<Food, String> {

    private Function<Integer, LiveData<Food>> updater;

    public FoodEditInteractor(BaseFragment owner,
                              BiFunction<Food, String, LiveData<StatusCode>> editor,
                              Function<Integer, LiveData<Food>> updater) {
        super(owner, editor);
        this.updater = updater;
    }

    @Override
    protected void treatErrorCode(StatusCode code, Food item, String editedData) {
        if (code == StatusCode.INVALID_DATA_VERSION) {
            LiveData<Food> newData = updater.apply(item.id);
            newData.observe(owner, newItem -> {
                if (newItem != null && !newItem.equals(item) && ! newItem.name.equals(item.name)) {
                    compareFood(item, editedData, newItem);
                    newData.removeObservers(owner);
                }
            });
        } else
            owner.maybeShowEditError(code);
    }

    private void compareFood(Food item, String localNewName, Food upstreamItem) {
        String message = owner.getString(R.string.error_food_changed_twice, item.name, localNewName, upstreamItem.name);
        owner.showErrorDialog(R.string.dialog_rename_food, message, (d,w) -> observeEditing(upstreamItem, localNewName));
    }

}
