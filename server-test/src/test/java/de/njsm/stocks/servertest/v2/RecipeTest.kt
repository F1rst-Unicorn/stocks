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

import de.njsm.stocks.client.business.*
import de.njsm.stocks.client.business.entities.*
import de.njsm.stocks.client.business.entities.Recipe
import de.njsm.stocks.client.business.entities.StatusCode
import de.njsm.stocks.common.api.*
import de.njsm.stocks.servertest.TestSuite
import de.njsm.stocks.servertest.v2.repo.*
import de.njsm.stocks.servertest.v2.repo.ScaledUnitRepository
import de.njsm.stocks.servertest.v2.repo.UnitRepository
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.ValidatableResponse
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant
import java.util.stream.Collectors
import javax.inject.Inject

@Order(1300)
class RecipeTest : Base() {
    internal lateinit var unitRepository: UnitRepository
        @Inject set

    internal lateinit var foodRepository: FoodRepository
        @Inject set

    internal lateinit var recipeAddService: RecipeAddService
        @Inject set

    internal lateinit var recipeDeleteService: RecipeDeleteService
        @Inject set

    internal lateinit var recipeEditService: RecipeEditService
        @Inject set

    internal lateinit var scaledUnitRepository: ScaledUnitRepository
        @Inject set

    @BeforeEach
    fun setUp() {
        dagger.inject(this)
    }

    @Test
    fun addingARecipeWorks() {
        val name = uniqueName
        val instructions = "instruction"
        val duration = Duration.ofHours(2)

        val id = recipeAddService.add(RecipeAddForm.create(
            name,
            instructions,
            duration,
            emptyList(),
            emptyList(),
        ))

        val recipes = updateService.getRecipes(Instant.EPOCH)
        assertThat(recipes).filteredOn(RecipeForSynchronisation::id, id.id())
            .isNotEmpty()
            .allMatch { it.name() == name }
            .allMatch { it.instructions() == instructions }
            .allMatch { it.duration() == duration }
    }

    @Test
    fun addingARecipeWithIngredientsAndProductsWorks() {
        putRecipeWithIngredientAndProduct()
    }

    @Test
    fun editingInvalidIdIsReported() {
        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                recipeEditService.edit(RecipeEditNetworkData.create(
                    RecipeEditBaseNetworkData.create(9999, 0, "", "", Duration.ZERO),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                ))
            }
            .matches { it.statusCode == StatusCode.NOT_FOUND }
    }

    @Test
    fun editingInvalidVersionIsReported() {
        val id = recipeAddService.add(RecipeAddForm.create(
            uniqueName,
            "instruction",
            Duration.ofHours(2),
            emptyList(),
            emptyList(),
        ))

        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                recipeEditService.edit(RecipeEditNetworkData.create(
                    RecipeEditBaseNetworkData.create(id.id(), 999, "", "", Duration.ZERO),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                ))
            }
            .matches { it.statusCode == StatusCode.INVALID_DATA_VERSION }
    }

    @Test
    fun validEditingWorks() {
        val id = putRecipeWithIngredientAndProduct()
        val newName = uniqueName
        val newInstructions = uniqueName
        val newDuration = Duration.ofHours(3)

        val ingredients = updateService.getRecipeIngredients(Instant.EPOCH)
            .stream()
            .filter { it.recipe() == id.id() }
            .filter { it.transactionTimeEnd() == Constants.INFINITY }
            .filter { it.validTimeStart().isBefore(Instant.now()) }
            .filter { it.validTimeEnd().isAfter(Instant.now()) }
            .map { RecipeIngredientEditNetworkData.create(
                it.id(),
                it.version(),
                it.amount() + 2,
                IdImpl.create(it.unit()),
                IdImpl.create(it.ingredient())) }
            .toList()
        val products = updateService.getRecipeProducts(Instant.EPOCH)
            .stream()
            .filter { it.recipe() == id.id() }
            .filter { it.transactionTimeEnd() == Constants.INFINITY }
            .filter { it.validTimeStart().isBefore(Instant.now()) }
            .filter { it.validTimeEnd().isAfter(Instant.now()) }
            .map { RecipeProductEditNetworkData.create(
                it.id(),
                it.version(),
                it.amount() + 1,
                IdImpl.create(it.unit()),
                IdImpl.create(it.product())) }
            .toList()

        recipeEditService.edit(RecipeEditNetworkData.create(
            RecipeEditBaseNetworkData.create(id.id(), 0, newName, newInstructions, newDuration),
            emptyList(),
            emptyList(),
            ingredients,
            emptyList(),
            emptyList(),
            products
        ))
    }

    @Test
    fun deletingInvalidIdIsReported() {
        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                recipeDeleteService.delete(RecipeDeleteData.create(
                    VersionedId.create(9999, 0),
                    emptyList(),
                    emptyList(),
                ))
            }
            .matches { it.statusCode == StatusCode.NOT_FOUND }
    }

    @Test
    fun deletingInvalidVersionIsReported() {
        val id = recipeAddService.add(RecipeAddForm.create(
            uniqueName,
            "instruction",
            Duration.ofHours(2),
            emptyList(),
            emptyList(),
        ))

        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                recipeDeleteService.delete(RecipeDeleteData.create(
                    VersionedId.create(id.id(), 999),
                    emptyList(),
                    emptyList(),
                ))
            }
            .matches { it.statusCode == StatusCode.INVALID_DATA_VERSION }
    }

    @Test
    fun validDeletionWorks() {
        val id = putRecipeWithIngredientAndProduct()
        val ingredients = updateService.getRecipeIngredients(Instant.EPOCH)
            .stream()
            .filter { it.recipe() == id.id() }
            .filter { it.transactionTimeEnd() == Constants.INFINITY }
            .filter { it.validTimeStart().isBefore(Instant.now()) }
            .filter { it.validTimeEnd().isAfter(Instant.now()) }
            .map { RecipeIngredientDeleteNetworkData.create(it.id(), it.version()) }
            .toList()
        val products = updateService.getRecipeProducts(Instant.EPOCH)
            .stream()
            .filter { it.recipe() == id.id() }
            .filter { it.transactionTimeEnd() == Constants.INFINITY }
            .filter { it.validTimeStart().isBefore(Instant.now()) }
            .filter { it.validTimeEnd().isAfter(Instant.now()) }
            .map { RecipeProductDeleteNetworkData.create(it.id(), it.version()) }
            .toList()

        recipeDeleteService.delete(RecipeDeleteData.create(
            VersionedId.create(id.id(), 0),
            ingredients,
            products,
        ))
    }

    private fun putRecipeWithIngredientAndProduct(): IdImpl<Recipe> {
        val foodId = foodRepository.createNewFood(uniqueName)
        val name = uniqueName
        val instructions = "instruction"
        val duration = Duration.ofHours(2)
        return recipeAddService.add(RecipeAddForm.create(
            name,
            instructions,
            duration,
            listOf(RecipeIngredientToAdd.create(
                1,
                foodId,
                scaledUnitRepository.anyScaledUnitId,
            )),
            listOf(RecipeProductToAdd.create(
                1,
                foodId,
                scaledUnitRepository.anyScaledUnitId,
            )),
        ))
    }
}
