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
import de.njsm.stocks.client.business.ScaledUnitAddService
import de.njsm.stocks.client.business.UpdateService
import de.njsm.stocks.client.business.entities.IdImpl
import de.njsm.stocks.client.business.entities.ScaledUnit
import de.njsm.stocks.client.business.entities.ScaledUnitAddForm
import java.math.BigDecimal
import java.time.Instant
import javax.inject.Inject
import de.njsm.stocks.client.business.entities.Unit as UnitOfMeasurement

class ScaledUnitRepository
    @Inject
    constructor(
        private val scaledUnitAddService: ScaledUnitAddService,
        private val unitRepository: UnitRepository,
        private val updateService: UpdateService,
    ) {
        val anyScaledUnitId: IdImpl<ScaledUnit>
            get() =
                updateService.getScaledUnits(Instant.EPOCH)
                    .stream()
                    .filter { it.transactionTimeEnd() == Constants.INFINITY }
                    .filter { it.validTimeStart().isBefore(Instant.now()) }
                    .filter { it.validTimeEnd().isAfter(Instant.now()) }
                    .findFirst()
                    .map { it.id() }
                    .map { IdImpl.create<ScaledUnit>(it) }
                    .orElseGet { createNew(BigDecimal.ONE) }

        fun createNew(
            scale: BigDecimal,
            unit: IdImpl<Unit> = unitRepository.anyUnitId,
        ): IdImpl<ScaledUnit> {
            return scaledUnitAddService.add(
                ScaledUnitAddForm.create(
                    scale,
                    unit.id(),
                ),
            )
        }
    }
