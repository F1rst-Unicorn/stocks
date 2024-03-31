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
package de.njsm.stocks.servertest.v2.repo

import de.njsm.stocks.client.business.Constants
import de.njsm.stocks.client.business.UnitAddService
import de.njsm.stocks.client.business.UpdateService
import de.njsm.stocks.client.business.entities.IdImpl
import de.njsm.stocks.client.business.entities.Unit
import de.njsm.stocks.client.business.entities.UnitAddForm
import de.njsm.stocks.client.business.entities.UnitForSynchronisation
import java.time.Instant
import javax.inject.Inject

class UnitRepository
    @Inject
    constructor(private val unitAddService: UnitAddService, private val updateService: UpdateService) {
        val anyUnitId: IdImpl<Unit>
            get() =
                updateService.getUnits(Instant.EPOCH)
                    .stream()
                    .filter { it.transactionTimeEnd() == Constants.INFINITY }
                    .filter { it.validTimeStart().isBefore(Instant.now()) }
                    .filter { it.validTimeEnd().isAfter(Instant.now()) }
                    .findFirst()
                    .map { it.id() }
                    .map { IdImpl.create<Unit>(it) }
                    .orElseGet { createNew("getAnyUnitId", "getAnyUnitId") }

        fun createNew(
            name: String,
            abbreviation: String?,
        ): IdImpl<Unit> {
            return unitAddService.addUnit(UnitAddForm.create(name, abbreviation))
        }
    }
