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
package de.njsm.stocks.servertest.v2

import de.njsm.stocks.client.business.Constants
import de.njsm.stocks.client.business.EntityDeleteService
import de.njsm.stocks.client.business.FoodAddService
import de.njsm.stocks.client.business.FoodEditService
import de.njsm.stocks.client.business.StatusCodeException
import de.njsm.stocks.client.business.entities.Food
import de.njsm.stocks.client.business.entities.FoodAddForm
import de.njsm.stocks.client.business.entities.FoodForDeletion
import de.njsm.stocks.client.business.entities.FoodForEditing
import de.njsm.stocks.client.business.entities.FoodForSynchronisation
import de.njsm.stocks.client.business.entities.StatusCode
import de.njsm.stocks.servertest.v2.repo.LocationRepository
import de.njsm.stocks.servertest.v2.repo.UnitRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.Period
import java.util.Optional
import javax.inject.Inject

@Order(800)
class FoodTest : Base() {
    internal lateinit var foodAddService: FoodAddService
        @Inject set

    internal lateinit var foodEditService: FoodEditService
        @Inject set

    internal lateinit var foodDeleteService: EntityDeleteService<Food>
        @Inject set

    internal lateinit var locationRepository: LocationRepository
        @Inject set

    internal lateinit var unitRepository: UnitRepository
        @Inject set

    @BeforeEach
    fun setUp() {
        dagger.inject(this)
    }

    @Test
    fun addFood() {
        val input = FoodAddForm.create(uniqueName, false, Period.ofDays(0), null, unitRepository.anyUnitId.id(), "")

        val id = foodAddService.add(input)

        val foods = updateService.getFood(Instant.EPOCH)
        assertThat(foods).filteredOn(FoodForSynchronisation::id, id.id())
            .isNotEmpty
            .allMatch { it.name() == input.name() }
            .allMatch { it.storeUnit() == input.storeUnit() }
            .allMatch { it.toBuy() == input.toBuy() }
    }

    @Test
    fun renameFood() {
        val newFood = FoodAddForm.create(uniqueName, false, Period.ofDays(0), null, unitRepository.anyUnitId.id(), "")
        val id = foodAddService.add(newFood)
        val locationId = locationRepository.createNewLocationType(uniqueName)
        val input =
            FoodForEditing.create(
                id.id(),
                0,
                uniqueName,
                true,
                newFood.expirationOffset().plusDays(1),
                Optional.of(locationId.id()),
                newFood.storeUnit(),
                uniqueName,
            )

        foodEditService.edit(input)

        val foods = updateService.getFood(Instant.EPOCH)
        assertThat(foods).filteredOn(FoodForSynchronisation::id, id.id())
            .filteredOn(FoodForSynchronisation::version, 1)
            .isNotEmpty
            .allMatch { it.name() == input.name() }
            .allMatch { it.storeUnit() == input.storeUnit() }
            .allMatch { it.toBuy() == input.toBuy() }
            .allMatch { it.expirationOffset() == input.expirationOffset() }
            .allMatch { it.description() == input.description() }
    }

    @Test
    fun settingFoodExpirationOffsetToZeroWorks() {
        val newFood = FoodAddForm.create(uniqueName, false, Period.ofDays(5), null, unitRepository.anyUnitId.id(), "")
        val id = foodAddService.add(newFood)
        val locationId = locationRepository.createNewLocationType(uniqueName)
        val input =
            FoodForEditing.create(
                id.id(),
                0,
                uniqueName,
                true,
                Period.ofDays(0),
                Optional.of(locationId.id()),
                newFood.storeUnit(),
                uniqueName,
            )

        foodEditService.edit(input)

        val foods = updateService.getFood(Instant.EPOCH)
        assertThat(foods).filteredOn(FoodForSynchronisation::id, id.id())
            .filteredOn(FoodForSynchronisation::version, 1)
            .isNotEmpty
            .allMatch { it.name() == input.name() }
            .allMatch { it.storeUnit() == input.storeUnit() }
            .allMatch { it.toBuy() == input.toBuy() }
            .allMatch { it.expirationOffset() == input.expirationOffset() }
            .allMatch { it.description() == input.description() }
    }

    @Test
    fun renamingFailsWithWrongVersion() {
        val newFood = FoodAddForm.create(uniqueName, false, Period.ofDays(0), null, unitRepository.anyUnitId.id(), "")
        val newName = uniqueName
        val id = foodAddService.add(newFood)
        val input = FoodForEditing.create(id.id(), 1, newName, true, Period.ZERO, Optional.empty(), 1, uniqueName)

        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                foodEditService.edit(input)
            }
            .matches { it.statusCode == StatusCode.INVALID_DATA_VERSION }
    }

    @Test
    fun renamingUnknownIdIsReported() {
        val input = FoodForEditing.create(9999, 0, uniqueName, true, Period.ZERO, Optional.empty(), 1, uniqueName)
        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                foodEditService.edit(input)
            }
            .matches { it.statusCode == StatusCode.NOT_FOUND }
    }

    @Test
    fun deleteFood() {
        val newFood = FoodAddForm.create(uniqueName, false, Period.ofDays(0), null, unitRepository.anyUnitId.id(), "")
        val id = foodAddService.add(newFood)

        foodDeleteService.delete(FoodForDeletion.create(id.id(), 0))

        val foods = updateService.getFood(Instant.EPOCH)
        assertThat(foods).filteredOn(FoodForSynchronisation::id, id.id())
            .isNotEmpty
            .anyMatch { it.transactionTimeEnd().isBefore(Constants.INFINITY) }
    }

    @Test
    fun deletingFailsWithWrongVersion() {
        val newFood = FoodAddForm.create(uniqueName, false, Period.ofDays(0), null, unitRepository.anyUnitId.id(), "")
        val id = foodAddService.add(newFood)
        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                foodDeleteService.delete(FoodForDeletion.create(id.id(), 9999))
            }
            .matches { it.statusCode == StatusCode.INVALID_DATA_VERSION }
    }

    @Test
    fun deletingUnknownIdIsReported() {
        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                foodDeleteService.delete(FoodForDeletion.create(9999, 0))
            }
            .matches { it.statusCode == StatusCode.NOT_FOUND }
    }
}
