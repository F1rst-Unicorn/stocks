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
import de.njsm.stocks.client.business.entities.UnitAddForm
import java.time.Instant
import javax.inject.Inject
import de.njsm.stocks.client.business.entities.Unit as UnitOfMeasurement

class UnitRepository
    @Inject
    constructor(private val unitAddService: UnitAddService, private val updateService: UpdateService) {
        val anyUnitId: IdImpl<UnitOfMeasurement>
            get() =
                updateService.getUnits(Instant.EPOCH, Constants.INFINITY)
                    .stream()
                    .filter { it.transactionTimeEnd() == Constants.INFINITY }
                    .filter { it.validTimeEnd().isAfter(Instant.now()) }
                    .findFirst()
                    .map { it.id() }
                    .map { IdImpl.create<UnitOfMeasurement>(it) }
                    .orElseGet { createNew("getAnyUnitId", "getAnyUnitId") }

        fun createNew(
            name: String,
            abbreviation: String?,
        ): IdImpl<UnitOfMeasurement> {
            return unitAddService.addUnit(UnitAddForm.create(name, abbreviation))
        }
    }
