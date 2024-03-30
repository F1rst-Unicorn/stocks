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
import de.njsm.stocks.client.business.FoodItemAddService
import de.njsm.stocks.client.business.FoodItemEditService
import de.njsm.stocks.client.business.StatusCodeException
import de.njsm.stocks.client.business.entities.FoodItem
import de.njsm.stocks.client.business.entities.FoodItemForDeletion
import de.njsm.stocks.client.business.entities.FoodItemForEditing
import de.njsm.stocks.client.business.entities.FoodItemForSynchronisation
import de.njsm.stocks.client.business.entities.FoodItemToAdd
import de.njsm.stocks.client.business.entities.StatusCode
import de.njsm.stocks.servertest.v2.repo.FoodItemRepository
import de.njsm.stocks.servertest.v2.repo.FoodRepository
import de.njsm.stocks.servertest.v2.repo.LocationRepository
import de.njsm.stocks.servertest.v2.repo.UnitRepository
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import java.time.Instant
import javax.inject.Inject

@Order(1200)
class FoodItemTest : Base() {
    internal lateinit var foodItemEditService: FoodItemEditService
        @Inject set

    internal lateinit var locationRepository: LocationRepository
        @Inject set

    internal lateinit var foodRepository: FoodRepository
        @Inject set

    internal lateinit var unitRepository: UnitRepository
        @Inject set

    internal lateinit var foodItemAddService: FoodItemAddService
        @Inject set

    internal lateinit var foodItemDeleteService: EntityDeleteService<FoodItem>
        @Inject set

    internal lateinit var foodItemRepository: FoodItemRepository
        @Inject set

    @BeforeEach
    fun setUp() {
        dagger.inject(this)
    }

    @Test
    fun addFoodItem() {
        val locationId = locationRepository.createNewLocationType(uniqueName)
        val foodId = foodRepository.createNewFood(uniqueName)
        val date = Instant.ofEpochMilli(14)

        foodItemAddService.add(FoodItemToAdd.create(date, foodId.id(), locationId.id(), unitRepository.anyUnitId.id()))

        val foodItems = updateService.getFoodItems(Instant.EPOCH)
        assertThat(foodItems).filteredOn(FoodItemForSynchronisation::storedIn, locationId.id())
            .isNotEmpty()
            .allMatch { it.eatBy() == date }
            .allMatch { it.registers() == 1 }
            .allMatch { it.buys() == 1 }
            .allMatch { it.ofType() == foodId.id() }
    }

    @Test
    fun editItem() {
        val locationId = locationRepository.createNewLocationType(uniqueName)
        val foodId = foodRepository.createNewFood(uniqueName)
        val movedLocation = locationRepository.createNewLocationType(uniqueName)
        val date = Instant.ofEpochMilli(14)
        val editedDate = Instant.ofEpochMilli(15)
        foodItemAddService.add(FoodItemToAdd.create(date, foodId.id(), locationId.id(), unitRepository.anyUnitId.id()))
        val addedItem =
            updateService.getFoodItems(Instant.EPOCH).stream()
                .filter { it.ofType() == foodId.id() }
                .findFirst()
                .get()

        foodItemEditService.edit(FoodItemForEditing.create(addedItem.id(), 0, editedDate, movedLocation.id(), addedItem.unit()))

        val foodItems =
            updateService.getFoodItems(Instant.EPOCH).stream()
                .filter { it.ofType() == foodId.id() }
                .toList()
        assertThat(foodItems).filteredOn(FoodItemForSynchronisation::ofType, foodId.id())
            .filteredOn(FoodItemForSynchronisation::version, 1)
            .hasSize(1)
            .allMatch { it.storedIn() == movedLocation.id() }
            .allMatch { it.eatBy() == editedDate }
    }

    @Test
    fun editInvalidVersionIsReported() {
        val locationId = locationRepository.createNewLocationType(uniqueName)
        val foodId = foodRepository.createNewFood(uniqueName)
        val movedLocation = locationRepository.createNewLocationType(uniqueName)
        val date = Instant.ofEpochMilli(14)
        val editedDate = Instant.ofEpochMilli(15)
        foodItemAddService.add(FoodItemToAdd.create(date, foodId.id(), locationId.id(), unitRepository.anyUnitId.id()))
        val addedItem =
            updateService.getFoodItems(Instant.EPOCH).stream()
                .filter { it.ofType() == foodId.id() }
                .findFirst()
                .get()

        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                foodItemEditService.edit(
                    FoodItemForEditing.create(addedItem.id(), 99, editedDate, movedLocation.id(), addedItem.unit()),
                )
            }
            .matches { it.statusCode == StatusCode.INVALID_DATA_VERSION }
    }

    @Test
    fun editInvalidIdIsReported() {
        val editedDate = Instant.ofEpochMilli(15)
        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                foodItemEditService.edit(FoodItemForEditing.create(9999, 0, editedDate, 2, 3))
            }
            .matches { it.statusCode == StatusCode.NOT_FOUND }
    }

    @Test
    fun deleteItem() {
        val locationId = locationRepository.createNewLocationType(uniqueName)
        val foodId = foodRepository.createNewFood(uniqueName)
        val id = foodItemRepository.createItem(locationId, foodId)

        foodItemDeleteService.delete(FoodItemForDeletion.create(id.id(), 0))

        val foodItems = updateService.getFoodItems(Instant.EPOCH)
        Assertions.assertThat(foodItems).filteredOn(FoodItemForSynchronisation::id, id.id())
            .isNotEmpty
            .anyMatch { it.transactionTimeEnd().isBefore(Constants.INFINITY) }
    }

    @Test
    fun deletingInvalidVersionIsReported() {
        val locationId = locationRepository.createNewLocationType(uniqueName)
        val foodId = foodRepository.createNewFood(uniqueName)
        val id = foodItemRepository.createItem(locationId, foodId)

        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                foodItemDeleteService.delete(FoodItemForDeletion.create(id.id(), 99))
            }
            .matches { it.statusCode == StatusCode.INVALID_DATA_VERSION }
    }

    @Test
    fun deletingUnknownIdIsReported() {
        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                foodItemDeleteService.delete(FoodItemForDeletion.create(999, 0))
            }
            .matches { it.statusCode == StatusCode.NOT_FOUND }
    }
}
