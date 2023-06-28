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
import de.njsm.stocks.client.business.ScaledUnitEditInteractor;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.ScaledUnit;
import de.njsm.stocks.client.business.entities.ScaledUnitEditingFormData;
import de.njsm.stocks.client.business.entities.ScaledUnitToEdit;

import javax.inject.Inject;

public class ScaledUnitEditViewModel extends ViewModel {

    private final ScaledUnitEditInteractor scaledUnitEditInteractor;

    private final ObservableDataCache<ScaledUnitEditingFormData> data;

    @Inject
    ScaledUnitEditViewModel(ScaledUnitEditInteractor scaledUnitEditInteractor, ObservableDataCache<ScaledUnitEditingFormData> data) {
        this.scaledUnitEditInteractor = scaledUnitEditInteractor;
        this.data = data;
    }

    public LiveData<ScaledUnitEditingFormData> getFormData(Id<ScaledUnit> id) {
        return data.getLiveData(() -> scaledUnitEditInteractor.getFormData(id));
    }

    public void edit(ScaledUnitToEdit editedScaledUnit) {
        scaledUnitEditInteractor.edit(editedScaledUnit);
    }

    @Override
    protected void onCleared() {
        data.clear();
    }
}
