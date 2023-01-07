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

import de.njsm.stocks.client.business.ScaledUnitRepository;
import de.njsm.stocks.client.business.entities.*;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ScaledUnitRepositoryImpl implements ScaledUnitRepository {

    private final ScaledUnitDao scaledUnitDao;

    @Inject
    ScaledUnitRepositoryImpl(ScaledUnitDao scaledUnitDao) {
        this.scaledUnitDao = scaledUnitDao;
    }

    @Override
    public Observable<List<ScaledUnitForListing>> getScaledUnits() {
        return scaledUnitDao.getCurrentScaledUnits()
                .map(v -> v.stream().map(w ->
                        ScaledUnitForListing.create(w.id(), w.abbreviation(), w.scale()))
                        .collect(toList()));
    }

    @Override
    public ScaledUnitForDeletion getEntityForDeletion(Id<ScaledUnit> id) {
        return scaledUnitDao.getScaledUnitForDeletion(id.id());
    }

    @Override
    public Observable<List<ScaledUnitForSelection>> getScaledUnitsForSelection() {
        return scaledUnitDao.getScaledUnitsForSelection()
                .distinctUntilChanged()
                .map(v -> v.stream().map(w ->
                                ScaledUnitForSelection.create(w.id(), w.abbreviation(), w.scale()))
                        .collect(toList()));
    }
}