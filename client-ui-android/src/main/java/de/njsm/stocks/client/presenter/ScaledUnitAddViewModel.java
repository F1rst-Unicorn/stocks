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
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.client.business.ScaledUnitAddInteractor;
import de.njsm.stocks.client.business.entities.ScaledUnitAddForm;
import de.njsm.stocks.client.business.entities.UnitForSelection;
import io.reactivex.rxjava3.core.BackpressureStrategy;

import javax.inject.Inject;
import java.util.List;

public class ScaledUnitAddViewModel extends ViewModel {

    private final ScaledUnitAddInteractor scaledUnitAddInteractor;

    @Inject
    ScaledUnitAddViewModel(ScaledUnitAddInteractor scaledUnitAddInteractor) {
        this.scaledUnitAddInteractor = scaledUnitAddInteractor;
    }

    public LiveData<List<UnitForSelection>> getUnits() {
        return LiveDataReactiveStreams.fromPublisher(
                scaledUnitAddInteractor.getUnits().toFlowable(BackpressureStrategy.LATEST));
    }

    public void addScaledUnit(ScaledUnitAddForm data) {
        scaledUnitAddInteractor.add(data);
    }
}
