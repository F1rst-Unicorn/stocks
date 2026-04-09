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
import de.njsm.stocks.client.business.GroceryChainAddService
import de.njsm.stocks.client.business.UpdateService
import de.njsm.stocks.client.business.entities.GroceryChain
import de.njsm.stocks.client.business.entities.GroceryChainAddForm
import de.njsm.stocks.client.business.entities.IdImpl
import de.njsm.stocks.client.business.entities.UnitAddForm
import java.time.Instant
import javax.inject.Inject

class GroceryChainRepository
    @Inject
    constructor(private val addService: GroceryChainAddService, private val updateService: UpdateService) {
        val anyGroceryChainId: IdImpl<GroceryChain>
            get() =
                updateService.getGroceryChains(Instant.EPOCH, Constants.INFINITY)
                    .stream()
                    .filter { it.transactionTimeEnd() == Constants.INFINITY }
                    .filter { it.validTimeEnd().isAfter(Instant.now()) }
                    .findFirst()
                    .map { it.id() }
                    .map { IdImpl.create<GroceryChain>(it) }
                    .orElseGet { createNew("getAnyGroceryChainId") }

        fun createNew(
            name: String,
        ): IdImpl<GroceryChain> {
            return addService.addGroceryChain(GroceryChainAddForm.create(name))
        }
    }
