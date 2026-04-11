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
import de.njsm.stocks.client.business.GroceryStoreAddService
import de.njsm.stocks.client.business.GroceryStoreEditService
import de.njsm.stocks.client.business.StatusCodeException
import de.njsm.stocks.client.business.entities.GroceryChainForDeletion
import de.njsm.stocks.client.business.entities.GroceryChainForSynchronisation
import de.njsm.stocks.client.business.entities.GroceryStore
import de.njsm.stocks.client.business.entities.GroceryStoreAddForm
import de.njsm.stocks.client.business.entities.GroceryStoreForDeletion
import de.njsm.stocks.client.business.entities.GroceryStoreForEditing
import de.njsm.stocks.client.business.entities.GroceryStoreForSynchronisation
import de.njsm.stocks.client.business.entities.StatusCode
import de.njsm.stocks.servertest.v2.repo.GroceryChainRepository
import de.njsm.stocks.servertest.v2.repo.GroceryStoreRepository
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import java.time.Instant
import javax.inject.Inject

@Order(900)
class GroceryStoreTest : Base() {
    internal lateinit var addService: GroceryStoreAddService
        @Inject set

    internal lateinit var editService: GroceryStoreEditService
        @Inject set

    internal lateinit var deleteService: EntityDeleteService<GroceryStore>
        @Inject set

    internal lateinit var repository: GroceryStoreRepository
        @Inject set

    internal lateinit var groceryChainRepository: GroceryChainRepository
        @Inject set

    @BeforeEach
    fun setUp() {
        dagger.inject(this)
    }

    @Test
    fun addAnItem() {
        val groceryChain = groceryChainRepository.createNew(uniqueName)
        val input = GroceryStoreAddForm.create(uniqueName, groceryChain.id())

        addService.addGroceryStore(input)

        val data = updateService.getGroceryStores(Instant.EPOCH, Constants.INFINITY)
        Assertions.assertThat(data).filteredOn(GroceryStoreForSynchronisation::name, input.name())
            .isNotEmpty
            .allMatch { it.name() == input.name() }
    }

    @Test
    fun rename() {
        val newName = uniqueName
        val id = repository.createNew(uniqueName)
        val modifiedGroceryChain = groceryChainRepository.createNew(uniqueName)

        editService.edit(GroceryStoreForEditing.create(id.id(), 0, newName, modifiedGroceryChain.id()))

        val data = updateService.getGroceryStores(Instant.EPOCH, Constants.INFINITY)
        Assertions.assertThat(data).filteredOn(GroceryStoreForSynchronisation::name, newName)
            .isNotEmpty
            .allMatch { it.name() == newName }
    }

    @Test
    fun renamingFailsWithWrongVersion() {
        val newName = uniqueName
        val id = repository.createNew(uniqueName)
        val modifiedGroceryChain = groceryChainRepository.createNew(uniqueName)

        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                editService.edit(
                    GroceryStoreForEditing.create(id.id(), 99, newName, modifiedGroceryChain.id()),
                )
            }
            .matches { it.statusCode == StatusCode.INVALID_DATA_VERSION }
    }

    @Test
    fun renamingUnknownIdIsReported() {
        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                editService.edit(
                    GroceryStoreForEditing.create(9999, 0, uniqueName, 1),
                )
            }
            .matches { it.statusCode == StatusCode.NOT_FOUND }
    }

    @Test
    fun delete() {
        val name = uniqueName
        val id = repository.createNew(name)

        deleteService.delete(GroceryStoreForDeletion.create(id.id(), 0))

        val data = updateService.getGroceryStores(Instant.EPOCH, Constants.INFINITY)
        Assertions.assertThat(data).filteredOn(GroceryStoreForSynchronisation::name, name)
            .isNotEmpty
            .anyMatch { it.transactionTimeEnd().isBefore(Constants.INFINITY) }
    }

    @Test
    fun deletingFailsWithWrongVersion() {
        val id = repository.createNew(uniqueName)

        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy { deleteService.delete(GroceryStoreForDeletion.create(id.id(), 99)) }
            .matches { it.statusCode == StatusCode.INVALID_DATA_VERSION }
    }

    @Test
    fun deletingUnknownIdIsReported() {
        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy { deleteService.delete(GroceryStoreForDeletion.create(9999, 0)) }
            .matches { it.statusCode == StatusCode.NOT_FOUND }
    }
}
