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

import de.njsm.stocks.client.business.FoodItemAddService
import de.njsm.stocks.client.business.UpdateService
import de.njsm.stocks.client.business.entities.Food
import de.njsm.stocks.client.business.entities.FoodItem
import de.njsm.stocks.client.business.entities.FoodItemToAdd
import de.njsm.stocks.client.business.entities.IdImpl
import de.njsm.stocks.client.business.entities.Location
import java.time.Instant
import javax.inject.Inject

class FoodItemRepository
    @Inject
    constructor(
        private val foodItemAddService: FoodItemAddService,
        private val unitRepository: UnitRepository,
    ) {
        fun createItem(
            location: IdImpl<Location>,
            food: IdImpl<Food>,
        ): IdImpl<FoodItem> {
            return foodItemAddService.add(FoodItemToAdd.create(Instant.EPOCH, food.id(), location.id(), unitRepository.anyUnitId.id()))
        }
    }
