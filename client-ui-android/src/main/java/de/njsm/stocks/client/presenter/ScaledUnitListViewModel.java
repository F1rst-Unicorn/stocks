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
import de.njsm.stocks.client.business.ScaledUnitListInteractor;
import de.njsm.stocks.client.business.entities.ScaledUnit;
import de.njsm.stocks.client.business.entities.ScaledUnitForListing;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;

public class ScaledUnitListViewModel extends ViewModel {

    private final ScaledUnitListInteractor scaledUnitListInteractor;

    private final EntityDeleter<ScaledUnit> scaledUnitDeleter;

    private final ObservableListCache<ScaledUnitForListing> data;

    @Inject
    ScaledUnitListViewModel(ScaledUnitListInteractor scaledUnitListInteractor, EntityDeleter<ScaledUnit> scaledUnitDeleter, ObservableListCache<ScaledUnitForListing> data) {
        this.scaledUnitListInteractor = scaledUnitListInteractor;
        this.scaledUnitDeleter = scaledUnitDeleter;
        this.data = data;
    }

    public LiveData<List<ScaledUnitForListing>> getScaledUnits() {
        return data.getLiveData(scaledUnitListInteractor::getScaledUnits);
    }

    public void deleteScaledUnit(int listItemIndex) {
        data.performOnListItem(listItemIndex, scaledUnitDeleter::delete);
    }

    public void resolveScaledUnitId(int listItemIndex, Consumer<Integer> callback) {
        data.performOnListItem(listItemIndex, t -> callback.accept(t.id()));
    }
}
