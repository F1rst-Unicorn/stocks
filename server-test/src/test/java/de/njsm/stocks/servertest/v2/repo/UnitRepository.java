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

package de.njsm.stocks.servertest.v2.repo;

import de.njsm.stocks.client.business.UnitAddService;
import de.njsm.stocks.client.business.UpdateService;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.client.business.entities.Unit;
import de.njsm.stocks.client.business.entities.UnitAddForm;

import javax.inject.Inject;
import java.time.Instant;

import static de.njsm.stocks.client.business.Constants.INFINITY;

public class UnitRepository {

    private final UnitAddService unitAddService;

    private final UpdateService updateService;

    @Inject
    public UnitRepository(UnitAddService unitAddService, UpdateService updateService) {
        this.unitAddService = unitAddService;
        this.updateService = updateService;
    }

    public IdImpl<Unit> getAnyUnitId() {
        return updateService.getUnits(Instant.EPOCH)
                .stream()
                .filter(v -> v.transactionTimeEnd().equals(INFINITY))
                .filter(v -> v.validTimeStart().isBefore(Instant.now()))
                .filter(v -> v.validTimeEnd().isAfter(Instant.now()))
                .findFirst()
                .map(Id::id)
                .<IdImpl<Unit>>map(IdImpl::create)
                .orElseGet(() -> createNew("getAnyUnitId", "getAnyUnitId"));
    }

    public IdImpl<Unit> createNew(String name, String abbreviation) {
        unitAddService.addUnit(UnitAddForm.create(name, abbreviation));
        return getIdOf(name);
    }

    private IdImpl<Unit> getIdOf(String name) {
        return updateService.getUnits(Instant.EPOCH)
                .stream()
                .filter(v -> v.name().equals(name))
                .map(v -> IdImpl.<Unit>create(v.id()))
                .findFirst()
                .orElseThrow();
    }
}
