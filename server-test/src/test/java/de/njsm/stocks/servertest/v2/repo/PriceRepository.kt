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
import de.njsm.stocks.client.business.PriceAddService
import de.njsm.stocks.client.business.UpdateService
import de.njsm.stocks.client.business.entities.Food
import de.njsm.stocks.client.business.entities.GroceryChain
import de.njsm.stocks.client.business.entities.GroceryStore
import de.njsm.stocks.client.business.entities.GroceryStoreAddForm
import de.njsm.stocks.client.business.entities.IdImpl
import de.njsm.stocks.client.business.entities.Price
import de.njsm.stocks.client.business.entities.PriceAddForm
import de.njsm.stocks.client.business.entities.ScaledUnit
import java.math.BigDecimal
import java.time.Instant
import javax.inject.Inject

class PriceRepository
    @Inject
    constructor(
        private val addService: PriceAddService,
        private val groceryStoreRepository: GroceryStoreRepository,
        private val foodRepository: FoodRepository,
        private val scaledUnitRepository: ScaledUnitRepository) {

        fun createNew(
            price: BigDecimal,
            scale: BigDecimal,
            groceryStore: IdImpl<GroceryStore> = groceryStoreRepository.anyGroceryStoreId,
            food: IdImpl<Food> = foodRepository.anyFoodId,
            scaledUnit: IdImpl<ScaledUnit> = scaledUnitRepository.anyScaledUnitId,
        ): IdImpl<Price> {
            return addService.addPrice(PriceAddForm.create(
                price,
                scale,
                groceryStore.id(),
                food.id(),
                scaledUnit.id()
            ))
        }
    }
