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
import de.njsm.stocks.android.db.views.FoodItemView;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.util.Config;

public class FoodItemDeletionInteractor extends DeletionInteractor<FoodItemView> {

    private final Function<Integer, LiveData<FoodItemView>> updater;

    public FoodItemDeletionInteractor(BaseFragment owner,
                                      Function<FoodItemView, LiveData<StatusCode>> deleter,
                                      Function<Integer, LiveData<FoodItemView>> updater) {
        super(owner, deleter);
        this.updater = updater;
    }

    @Override
    protected void treatErrorCode(StatusCode code, FoodItemView item) {
        if (code == StatusCode.INVALID_DATA_VERSION) {
            LiveData<FoodItemView> newData = updater.apply(item.id);
            newData.observe(owner, newItem -> {
                if (newItem != null && !newItem.equals(item)) {
                    compareFood(item, newItem);
                    newData.removeObservers(owner);
                }
            });
        } else
            super.treatErrorCode(code, item);
    }

    private void compareFood(FoodItemView item, FoodItemView newItem) {
        boolean datesEqual = item.getEatByDate().equals(newItem.getEatByDate());
        boolean locationsEqual = item.getLocation().equals(newItem.getLocation());

        String message;
        if (datesEqual) {
            if (locationsEqual) {
                return;
            } else {
                message = owner.getString(R.string.dialog_item_location_changed,
                        item.getLocation(),
                        newItem.getLocation());
            }
        } else {
            if (locationsEqual) {
                message = owner.getString(R.string.dialog_item_date_changed,
                        item.getEatByDate(),
                        newItem.getEatByDate());

            } else {
                message = owner.getString(R.string.dialog_item_both_changed,
                        item.getLocation(),
                        newItem.getLocation(),
                        Config.PRETTY_DATE_FORMAT.format(item.getEatByDate()),
                        Config.PRETTY_DATE_FORMAT.format(newItem.getEatByDate()));
            }
        }
        owner.showErrorDialog(R.string.title_consume,
                message,
                (d,w) -> observeDeletion(newItem));
    }
}
