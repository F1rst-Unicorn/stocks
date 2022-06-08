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

package de.njsm.stocks.client.business;

import de.njsm.stocks.client.business.entities.UnitForSelection;
import de.njsm.stocks.client.business.entities.conflict.ScaledUnitEditConflictData;
import de.njsm.stocks.client.business.entities.conflict.ScaledUnitEditConflictFormData;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

class ScaledUnitConflictInteractorImpl implements ScaledUnitConflictInteractor {

    private final ScaledUnitEditRepository scaledUnitEditRepository;

    private final ConflictRepository conflictRepository;

    @Inject
    ScaledUnitConflictInteractorImpl(ScaledUnitEditRepository scaledUnitEditRepository, ConflictRepository conflictRepository) {
        this.scaledUnitEditRepository = scaledUnitEditRepository;
        this.conflictRepository = conflictRepository;
    }

    @Override
    public Observable<ScaledUnitEditConflictFormData> getScaledUnitEditConflict(long errorId) {
        Observable<List<UnitForSelection>> units = scaledUnitEditRepository.getUnitsForSelection();
        Observable<ScaledUnitEditConflictData> scaledUnitData = conflictRepository.getScaledUnitEditConflict(errorId);

        return units.zipWith(scaledUnitData, (unitList, scaledUnit) -> ScaledUnitEditConflictFormData.create(scaledUnit, unitList));
    }
}
