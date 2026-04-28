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
import de.njsm.stocks.client.business.GroceryStoreAddService
import de.njsm.stocks.client.business.UpdateService
import de.njsm.stocks.client.business.entities.GroceryChain
import de.njsm.stocks.client.business.entities.GroceryStore
import de.njsm.stocks.client.business.entities.GroceryStoreAddForm
import de.njsm.stocks.client.business.entities.IdImpl
import java.time.Instant
import javax.inject.Inject

class GroceryStoreRepository
    @Inject
    constructor(
        private val addService: GroceryStoreAddService,
        private val groceryChainRepository: GroceryChainRepository,
        private val updateService: UpdateService) {
        val anyGroceryStoreId: IdImpl<GroceryStore>
            get() =
                updateService.getGroceryStores(Instant.EPOCH, Constants.INFINITY)
                    .stream()
                    .filter { it.transactionTimeEnd() == Constants.INFINITY }
                    .filter { it.validTimeEnd().isAfter(Instant.now()) }
                    .findFirst()
                    .map { it.id() }
                    .map { IdImpl.create<GroceryStore>(it) }
                    .orElseGet { createNew("getAnyGroceryStoreId") }

        fun createNew(
            name: String,
            groceryChain: IdImpl<GroceryChain> = groceryChainRepository.anyGroceryChainId
        ): IdImpl<GroceryStore> {
            return addService.addGroceryStore(GroceryStoreAddForm.create(name, groceryChain))
        }
    }
