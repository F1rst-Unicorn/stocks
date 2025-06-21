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
import de.njsm.stocks.client.business.RecipeDeleteService
import de.njsm.stocks.client.business.UserDeviceAddService
import de.njsm.stocks.client.business.entities.EanNumber
import de.njsm.stocks.client.business.entities.Food
import de.njsm.stocks.client.business.entities.FoodItem
import de.njsm.stocks.client.business.entities.IdImpl
import de.njsm.stocks.client.business.entities.Location
import de.njsm.stocks.client.business.entities.RecipeDeleteData
import de.njsm.stocks.client.business.entities.RecipeIngredientDeleteNetworkData
import de.njsm.stocks.client.business.entities.RecipeIngredientForSynchronisation
import de.njsm.stocks.client.business.entities.RecipeProductDeleteNetworkData
import de.njsm.stocks.client.business.entities.RecipeProductForSynchronisation
import de.njsm.stocks.client.business.entities.ScaledUnit
import de.njsm.stocks.client.business.entities.Unit
import de.njsm.stocks.client.business.entities.User
import de.njsm.stocks.client.business.entities.UserDevice
import de.njsm.stocks.client.business.entities.UserDeviceAddForm
import de.njsm.stocks.client.business.entities.VersionedId
import de.njsm.stocks.servertest.v2.repo.RecipeRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.MethodOrderer.MethodName
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import java.io.FileWriter
import java.time.Instant
import javax.inject.Inject

@TestMethodOrder(MethodName::class)
@Order(1600)
class Cleanup : Base() {
    internal lateinit var userDeviceAddService: UserDeviceAddService
        @Inject set

    internal lateinit var userDeviceDeleteService: EntityDeleteService<UserDevice>
        @Inject set

    internal lateinit var recipeRepository: RecipeRepository
        @Inject set

    internal lateinit var recipeDeleteService: RecipeDeleteService
        @Inject set

    internal lateinit var eanNumberDeleteService: EntityDeleteService<EanNumber>
        @Inject set

    internal lateinit var foodItemDeleteService: EntityDeleteService<FoodItem>
        @Inject set

    internal lateinit var foodDeleteService: EntityDeleteService<Food>
        @Inject set

    internal lateinit var locationDeleteService: EntityDeleteService<Location>
        @Inject set

    internal lateinit var scaledUnitDeleteService: EntityDeleteService<ScaledUnit>
        @Inject set

    internal lateinit var unitDeleteService: EntityDeleteService<Unit>
        @Inject set

    internal lateinit var userDeleteService: EntityDeleteService<User>
        @Inject set

    @BeforeEach
    fun setUp() {
        dagger.inject(this)
    }

    @Test
    fun clean01Devices() {
        val entities =
            updateService.getUserDevices(Instant.EPOCH, Constants.INFINITY)
                .filter { it.transactionTimeEnd() == Constants.INFINITY }
                .filter { it.validTimeStart().isBefore(Instant.now()) }
                .filter { it.validTimeEnd().isAfter(Instant.now()) }
                .toList()

        val ignoredDevices = listOf(1, 2)

        for (entity in entities) {
            if (ignoredDevices.contains(entity.id())) continue
            userDeviceDeleteService.delete(entity)
        }
    }

    @Test
    fun clean02Ean() {
        val entities =
            updateService.getEanNumbers(Instant.EPOCH, Constants.INFINITY)
                .filter { it.transactionTimeEnd() == Constants.INFINITY }
                .filter { it.validTimeStart().isBefore(Instant.now()) }
                .filter { it.validTimeEnd().isAfter(Instant.now()) }
                .toList()

        for (entity in entities) {
            eanNumberDeleteService.delete(entity)
        }
    }

    @Test
    fun clean03FoodItems() {
        val entities =
            updateService.getFoodItems(Instant.EPOCH, Constants.INFINITY)
                .filter { it.transactionTimeEnd() == Constants.INFINITY }
                .filter { it.validTimeStart().isBefore(Instant.now()) }
                .filter { it.validTimeEnd().isAfter(Instant.now()) }
                .toList()

        for (entity in entities) {
            foodItemDeleteService.delete(entity)
        }
    }

    @Test
    fun clean04Recipes() {
        val data = recipeRepository.getAll()

        for (d in data) {
            val ingredients = updateService.getRecipeIngredients(Instant.EPOCH, Constants.INFINITY)
            val x =
                ingredients
                    .stream()
                    .filter { it: RecipeIngredientForSynchronisation -> it.recipe() == d.id() }
                    .filter { it: RecipeIngredientForSynchronisation -> it.transactionTimeEnd() == Constants.INFINITY }
                    .filter { it: RecipeIngredientForSynchronisation ->
                        it.validTimeStart().isBefore(
                            Instant.now(),
                        )
                    }
                    .filter { it: RecipeIngredientForSynchronisation ->
                        it.validTimeEnd().isAfter(
                            Instant.now(),
                        )
                    }
                    .map { it: RecipeIngredientForSynchronisation ->
                        RecipeIngredientDeleteNetworkData.create(
                            it.id(),
                            it.version(),
                        )
                    }
                    .toList()
            val products =
                updateService.getRecipeProducts(Instant.EPOCH, Constants.INFINITY)
                    .stream()
                    .filter { it: RecipeProductForSynchronisation -> it.recipe() == d.id() }
                    .filter { it: RecipeProductForSynchronisation -> it.transactionTimeEnd() == Constants.INFINITY }
                    .filter { it: RecipeProductForSynchronisation ->
                        it.validTimeStart().isBefore(
                            Instant.now(),
                        )
                    }
                    .filter { it: RecipeProductForSynchronisation ->
                        it.validTimeEnd().isAfter(
                            Instant.now(),
                        )
                    }
                    .map { it: RecipeProductForSynchronisation ->
                        RecipeProductDeleteNetworkData.create(
                            it.id(),
                            it.version(),
                        )
                    }
                    .toList()

            recipeDeleteService.delete(
                RecipeDeleteData.create(
                    VersionedId.create(d.id(), d.version()),
                    x,
                    products,
                ),
            )
        }
    }

    @Test
    fun clean05Food() {
        val entities =
            updateService.getFood(Instant.EPOCH, Constants.INFINITY)
                .filter { it.transactionTimeEnd() == Constants.INFINITY }
                .filter { it.validTimeStart().isBefore(Instant.now()) }
                .filter { it.validTimeEnd().isAfter(Instant.now()) }
                .toList()

        for (entity in entities) {
            foodDeleteService.delete(entity)
        }
    }

    @Test
    fun clean06Locations() {
        val entities =
            updateService.getLocations(Instant.EPOCH, Constants.INFINITY)
                .filter { it.transactionTimeEnd() == Constants.INFINITY }
                .filter { it.validTimeStart().isBefore(Instant.now()) }
                .filter { it.validTimeEnd().isAfter(Instant.now()) }
                .toList()

        for (entity in entities) {
            locationDeleteService.delete(entity)
        }
    }

    @Test
    fun clean07ScaledUnits() {
        val entities =
            updateService.getScaledUnits(Instant.EPOCH, Constants.INFINITY)
                .filter { it.transactionTimeEnd() == Constants.INFINITY }
                .filter { it.validTimeStart().isBefore(Instant.now()) }
                .filter { it.validTimeEnd().isAfter(Instant.now()) }
                .toList()

        for (entity in entities) {
            if (entity.id() == 1) continue
            scaledUnitDeleteService.delete(entity)
        }
    }

    @Test
    fun clean08Units() {
        val entities =
            updateService.getUnits(Instant.EPOCH, Constants.INFINITY)
                .filter { it.transactionTimeEnd() == Constants.INFINITY }
                .filter { it.validTimeStart().isBefore(Instant.now()) }
                .filter { it.validTimeEnd().isAfter(Instant.now()) }
                .toList()

        for (entity in entities) {
            if (entity.id() == 1) continue
            unitDeleteService.delete(entity)
        }
    }

    @Test
    fun clean09Users() {
        val entities =
            updateService.getUsers(Instant.EPOCH, Constants.INFINITY)
                .filter { it.transactionTimeEnd() == Constants.INFINITY }
                .filter { it.validTimeStart().isBefore(Instant.now()) }
                .filter { it.validTimeEnd().isAfter(Instant.now()) }
                .toList()
        val ignoredUsers = listOf(1, 2)

        for (entity in entities) {
            if (ignoredUsers.contains(entity.id())) continue
            userDeleteService.delete(entity)
        }
    }

    @Test
    fun setupOtherTestAccounts() {
        val ticket1 =
            userDeviceAddService.add(
                UserDeviceAddForm.create(
                    "cli-client",
                    IdImpl.create(1),
                ),
            )
        val ticket2 =
            userDeviceAddService.add(
                UserDeviceAddForm.create(
                    "android-client",
                    IdImpl.create(1),
                ),
            )

        writeToFile("build/01_ticket", ticket1.ticket())
        writeToFile("build/01_id", ticket1.id().id().toString())
        writeToFile("build/02_ticket", ticket2.ticket())
        writeToFile("build/02_id", ticket2.id().id().toString())
    }

    private fun writeToFile(
        filename: String,
        content: String,
    ) {
        val writer = FileWriter(filename)
        writer.write(content)
        writer.close()
    }
}
