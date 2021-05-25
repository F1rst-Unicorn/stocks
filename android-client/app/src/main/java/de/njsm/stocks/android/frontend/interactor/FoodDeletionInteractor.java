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

import android.view.View;
import androidx.arch.core.util.Function;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.network.server.StatusCode;

public class FoodDeletionInteractor extends SnackbarDeletionInteractor<Food> {

    private final Function<Integer, LiveData<Food>> updater;

    private final int snackBarMessageId;

    public FoodDeletionInteractor(BaseFragment owner,
                                  View snackbarParent,
                                  Consumer<Food> deletionCancler,
                                  Function<Food, LiveData<StatusCode>> deleter,
                                  Function<Integer, LiveData<Food>> updater) {
        this(owner, snackbarParent, R.string.dialog_food_was_deleted, deletionCancler, deleter, updater);
    }

    public FoodDeletionInteractor(BaseFragment owner,
                                  View snackbarParent,
                                  int snackBarMessageId,
                                  Consumer<Food> deletionCancler,
                                  Function<Food, LiveData<StatusCode>> deleter,
                                  Function<Integer, LiveData<Food>> updater) {
        super(owner, deleter, deletionCancler, snackbarParent);
        this.updater = updater;
        this.snackBarMessageId = snackBarMessageId;
    }

    @Override
    protected int getSnackbarMessageId() {
        return snackBarMessageId;
    }

    @Override
    protected void treatErrorCode(StatusCode code, Food item) {
        if (code == StatusCode.INVALID_DATA_VERSION) {
            LiveData<Food> newData = updater.apply(item.id);
            newData.observe(owner, newItem -> {
                if (newItem != null && !newItem.equals(item)) {
                    compareFood(item, newItem);
                    newData.removeObservers(owner);
                }
            });
        } else
            super.treatErrorCode(code, item);
    }

    private void compareFood(Food item, Food upstreamItem) {
        String message = owner.requireContext().getString(R.string.error_food_changed, item.name, upstreamItem.name);
        owner.showErrorDialog(R.string.title_delete_food, message, (d, w) -> observeDeletion(upstreamItem));
    }
}
