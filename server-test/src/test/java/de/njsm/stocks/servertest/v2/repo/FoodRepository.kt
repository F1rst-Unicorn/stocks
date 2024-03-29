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

import de.njsm.stocks.client.business.FoodAddService
import de.njsm.stocks.client.business.UpdateService
import de.njsm.stocks.client.business.entities.Food
import de.njsm.stocks.client.business.entities.FoodAddForm
import de.njsm.stocks.client.business.entities.IdImpl
import de.njsm.stocks.servertest.v2.FoodTest
import java.time.Instant
import java.time.Period
import javax.inject.Inject

class FoodRepository
    @Inject
    constructor(
        private val foodAddService: FoodAddService,
        private val updateService: UpdateService,
        private val unitRepository: UnitRepository,
    ) {
        fun createNewFood(name: String): IdImpl<Food> {
            foodAddService.add(FoodAddForm.create(name, false, Period.ZERO, null, unitRepository.anyUnitId.id(), ""))
            return getIdOfFood(name)
        }

        private fun getIdOfFood(name: String): IdImpl<Food> {
            val food = updateService.getFood(Instant.EPOCH)
            return food.stream()
                .filter { it.name() == name }
                .findFirst()
                .map { IdImpl.create<Food>(it.id()) }
                .orElseThrow()
        }

        companion object {
            @JvmStatic
            val anyFoodId: Int
                get() {
                    val ids =
                        FoodTest.assertOnFood(false)
                            .extract()
                            .jsonPath()
                            .getList<Int>("data.id")

                    return if (ids.isEmpty()) {
                        FoodTest.createNewFoodType("getAnyFoodId")
                    } else {
                        ids[0]
                    }
                }
        }
    }
