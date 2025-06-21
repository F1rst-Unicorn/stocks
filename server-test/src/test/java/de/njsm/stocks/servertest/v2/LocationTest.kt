/* stocks is client-server program to manage a household's food stock
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
 */
package de.njsm.stocks.servertest.v2

import de.njsm.stocks.client.business.Constants
import de.njsm.stocks.client.business.EntityDeleteService
import de.njsm.stocks.client.business.LocationAddService
import de.njsm.stocks.client.business.LocationEditService
import de.njsm.stocks.client.business.StatusCodeException
import de.njsm.stocks.client.business.entities.Location
import de.njsm.stocks.client.business.entities.LocationAddForm
import de.njsm.stocks.client.business.entities.LocationForDeletion
import de.njsm.stocks.client.business.entities.LocationForEditing
import de.njsm.stocks.client.business.entities.LocationForSynchronisation
import de.njsm.stocks.client.business.entities.StatusCode
import de.njsm.stocks.servertest.v2.repo.FoodItemRepository
import de.njsm.stocks.servertest.v2.repo.FoodRepository
import de.njsm.stocks.servertest.v2.repo.LocationRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import java.time.Instant
import javax.inject.Inject

@Order(600)
class LocationTest : Base() {
    internal lateinit var locationAddService: LocationAddService
        @Inject set

    internal lateinit var locationEditService: LocationEditService
        @Inject set

    internal lateinit var locationDeleteService: EntityDeleteService<Location>
        @Inject set

    internal lateinit var locationRepository: LocationRepository
        @Inject set

    internal lateinit var foodRepository: FoodRepository
        @Inject set

    internal lateinit var foodItemRepository: FoodItemRepository
        @Inject set

    @BeforeEach
    fun inject() {
        dagger.inject(this)
    }

    @Test
    fun addAnItem() {
        val name = uniqueName

        val id = locationAddService.add(LocationAddForm.create(name, uniqueName))

        val locations = updateService.getLocations(Instant.EPOCH, Constants.INFINITY)
        assertThat(locations).filteredOn(LocationForSynchronisation::id, id.id())
            .isNotEmpty()
            .allMatch { it.initiates() == 1 }
            .allMatch { it.name() == name }
    }

    @Test
    fun renameLocation() {
        val name = uniqueName
        val newName = uniqueName
        val id = locationRepository.createNewLocationType(name)
        val input =
            LocationForEditing.builder()
                .id(id.id())
                .version(0)
                .name(newName)
                .description(uniqueName)
                .build()

        locationEditService.editLocation(input)

        val locations = updateService.getLocations(Instant.EPOCH, Constants.INFINITY)
        assertThat(locations).filteredOn(LocationForSynchronisation::name, newName)
            .isNotEmpty()
            .allMatch { it.description() == input.description() }
    }

    @Test
    fun renamingFailsWithWrongVersion() {
        val name = uniqueName
        val newName = uniqueName
        val id = locationRepository.createNewLocationType(name)

        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                locationEditService.editLocation(
                    LocationForEditing.builder()
                        .id(id.id())
                        .version(99)
                        .name(newName)
                        .description("")
                        .build(),
                )
            }
            .matches { it.statusCode == StatusCode.INVALID_DATA_VERSION }
    }

    @Test
    fun renamingUnknownIdIsReported() {
        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                locationEditService.editLocation(
                    LocationForEditing.builder()
                        .id(9999)
                        .version(0)
                        .name(uniqueName)
                        .description(uniqueName)
                        .build(),
                )
            }
            .matches { it.statusCode == StatusCode.NOT_FOUND }
    }

    @Test
    fun deleteLocation() {
        val name = uniqueName
        val id = locationRepository.createNewLocationType(name)

        locationDeleteService.delete(
            LocationForDeletion.builder()
                .id(id.id())
                .version(0)
                .build(),
        )

        val locations = updateService.getLocations(Instant.EPOCH, Constants.INFINITY)
        assertThat(locations).filteredOn(LocationForSynchronisation::name, name)
            .isNotEmpty
            .anyMatch { it.transactionTimeEnd().isBefore(Constants.INFINITY) }
    }

    @Test
    fun deletingFailsWithWrongVersion() {
        val name = uniqueName
        val id = locationRepository.createNewLocationType(name)

        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                locationDeleteService.delete(
                    LocationForDeletion.builder()
                        .id(id.id())
                        .version(99)
                        .build(),
                )
            }
            .matches { it.statusCode == StatusCode.INVALID_DATA_VERSION }
    }

    @Test
    fun deletingUnknownIdIsReported() {
        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                locationDeleteService.delete(
                    LocationForDeletion.builder()
                        .id(99999)
                        .version(0)
                        .build(),
                )
            }
            .matches { it.statusCode == StatusCode.NOT_FOUND }
    }

    @Test
    fun deleteWhileContainingFoodFails() {
        val location = locationRepository.createNewLocationType(uniqueName)
        val food = foodRepository.createNewFood(uniqueName)
        foodItemRepository.createItem(location, food)

        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                locationDeleteService.delete(
                    LocationForDeletion.builder()
                        .id(location.id())
                        .version(0)
                        .build(),
                )
            }
            .matches { it.statusCode == StatusCode.FOREIGN_KEY_CONSTRAINT_VIOLATION }
    }
}
