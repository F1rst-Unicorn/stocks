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
import de.njsm.stocks.client.business.PriceAddService
import de.njsm.stocks.client.business.StatusCodeException
import de.njsm.stocks.client.business.entities.GroceryStoreAddForm
import de.njsm.stocks.client.business.entities.GroceryStoreForDeletion
import de.njsm.stocks.client.business.entities.GroceryStoreForSynchronisation
import de.njsm.stocks.client.business.entities.Price
import de.njsm.stocks.client.business.entities.PriceAddForm
import de.njsm.stocks.client.business.entities.PriceForDeletion
import de.njsm.stocks.client.business.entities.PriceForSynchronisation
import de.njsm.stocks.client.business.entities.StatusCode
import de.njsm.stocks.servertest.v2.repo.FoodRepository
import de.njsm.stocks.servertest.v2.repo.GroceryStoreRepository
import de.njsm.stocks.servertest.v2.repo.PriceRepository
import de.njsm.stocks.servertest.v2.repo.ScaledUnitRepository
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import javax.inject.Inject

@Order(900)
class PriceTest : Base() {
    internal lateinit var addService: PriceAddService
        @Inject set

    internal lateinit var deleteService: EntityDeleteService<Price>
        @Inject set

    internal lateinit var repository: PriceRepository
        @Inject set

    internal lateinit var groceryStoreRepository: GroceryStoreRepository
        @Inject set

    internal lateinit var foodRepository: FoodRepository
        @Inject set

    internal lateinit var scaledUnitRepository: ScaledUnitRepository
        @Inject set

    @BeforeEach
    fun setUp() {
        dagger.inject(this)
    }

    @Test
    fun addAnItem() {
        val price = BigDecimal.valueOf(uniqueName.hashCode().toLong())
        val scale = BigDecimal.valueOf(uniqueName.hashCode().toLong())
        val groceryStore = groceryStoreRepository.createNew(uniqueName)
        val food = foodRepository.createNew(uniqueName)
        val scaledUnit = scaledUnitRepository.createNew(BigDecimal.ONE)
        val input = PriceAddForm.create(price, scale, groceryStore.id(), food.id(), scaledUnit.id())

        addService.addPrice(input)

        val data = updateService.getPrices(Instant.EPOCH, Constants.INFINITY)
        Assertions.assertThat(data).filteredOn(PriceForSynchronisation::price, price)
            .isNotEmpty
            .allMatch { it.price().compareTo(input.price()) == 0 }
    }

    @Test
    fun delete() {
        val price = BigDecimal.valueOf(uniqueName.hashCode().toLong())
        val id = repository.createNew(
            price,
            BigDecimal.valueOf(uniqueName.hashCode().toLong()))

        deleteService.delete(PriceForDeletion.create(id.id(), 0))

        val data = updateService.getPrices(Instant.EPOCH, Constants.INFINITY)
        Assertions.assertThat(data).filteredOn(PriceForSynchronisation::price, price)
            .isNotEmpty
            .anyMatch { it.transactionTimeEnd().isBefore(Constants.INFINITY) }
    }

    @Test
    fun deletingFailsWithWrongVersion() {
        val id = repository.createNew(
            BigDecimal.valueOf(uniqueName.hashCode().toLong()),
            BigDecimal.valueOf(uniqueName.hashCode().toLong()))

        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy { deleteService.delete(PriceForDeletion.create(id.id(), 99)) }
            .matches { it.statusCode == StatusCode.INVALID_DATA_VERSION }
    }

    @Test
    fun deletingUnknownIdIsReported() {
        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy { deleteService.delete(PriceForDeletion.create(9999, 0)) }
            .matches { it.statusCode == StatusCode.NOT_FOUND }
    }
}
