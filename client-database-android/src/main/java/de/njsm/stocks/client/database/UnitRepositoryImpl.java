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

package de.njsm.stocks.client.database;

import de.njsm.stocks.client.business.ScaledUnitAddRepository;
import de.njsm.stocks.client.business.UnitRepository;
import de.njsm.stocks.client.business.entities.*;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

class UnitRepositoryImpl implements UnitRepository, ScaledUnitAddRepository {

    private final UnitDao unitDao;

    @Inject
    UnitRepositoryImpl(UnitDao unitDao) {
        this.unitDao = unitDao;
    }

    @Override
    public Observable<List<UnitForListing>> getUnits() {
        return unitDao.getCurrentUnits()
                .distinctUntilChanged();
    }

    @Override
    public Observable<UnitToEdit> getUnit(Identifiable<Unit> id) {
        return unitDao.getUnitToEdit(id.id());
    }

    @Override
    public UnitForEditing getCurrentDataBeforeEditing(Identifiable<Unit> data) {
        return unitDao.getUnitForEditing(data.id());
    }

    @Override
    public UnitForDeletion getEntityForDeletion(Identifiable<Unit> id) {
        return unitDao.getUnit(id.id());
    }

    @Override
    public Observable<List<UnitForSelection>> getUnitsForSelection() {
        return unitDao.getCurrentUnitsForSelection()
                .distinctUntilChanged();
    }
}
