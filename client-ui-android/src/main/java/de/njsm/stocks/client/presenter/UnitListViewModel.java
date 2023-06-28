/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.client.presenter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.client.business.EntityDeleter;
import de.njsm.stocks.client.business.UnitListInteractor;
import de.njsm.stocks.client.business.entities.Unit;
import de.njsm.stocks.client.business.entities.UnitForListing;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;

public class UnitListViewModel extends ViewModel {

    private final UnitListInteractor unitListInteractor;

    private final EntityDeleter<Unit> unitDeleter;

    private final ObservableListCache<UnitForListing> data;

    @Inject
    UnitListViewModel(UnitListInteractor unitListInteractor, EntityDeleter<Unit> unitDeleter, ObservableListCache<UnitForListing> data) {
        this.unitListInteractor = unitListInteractor;
        this.unitDeleter = unitDeleter;
        this.data = data;
    }

    public LiveData<List<UnitForListing>> getUnits() {
        return data.getLiveData(unitListInteractor::getUnits);
    }

    public void deleteUnit(int listItemIndex) {
        data.performOnListItem(listItemIndex, unitDeleter::delete);
    }

    public void resolveUnitId(int listItemIndex, Consumer<Integer> callback) {
        data.performOnListItem(listItemIndex, t -> callback.accept(t.id()));
    }

    @Override
    protected void onCleared() {
        data.clear();
    }
}
