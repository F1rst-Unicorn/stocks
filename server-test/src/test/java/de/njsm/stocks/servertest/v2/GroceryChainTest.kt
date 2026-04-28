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
import de.njsm.stocks.client.business.GroceryChainAddService
import de.njsm.stocks.client.business.GroceryChainEditService
import de.njsm.stocks.client.business.StatusCodeException
import de.njsm.stocks.client.business.entities.GroceryChain
import de.njsm.stocks.client.business.entities.GroceryChainAddForm
import de.njsm.stocks.client.business.entities.GroceryChainForDeletion
import de.njsm.stocks.client.business.entities.GroceryChainForEditing
import de.njsm.stocks.client.business.entities.GroceryChainForSynchronisation
import de.njsm.stocks.client.business.entities.StatusCode
import de.njsm.stocks.client.business.entities.VersionedId
import de.njsm.stocks.servertest.v2.repo.GroceryChainRepository
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import java.time.Instant
import javax.inject.Inject

@Order(900)
class GroceryChainTest : Base() {
    internal lateinit var addService: GroceryChainAddService
        @Inject set

    internal lateinit var editService: GroceryChainEditService
        @Inject set

    internal lateinit var deleteService: EntityDeleteService<GroceryChain>
        @Inject set

    internal lateinit var repository: GroceryChainRepository
        @Inject set

    @BeforeEach
    fun setUp() {
        dagger.inject(this)
    }

    @Test
    fun addAnItem() {
        val input = GroceryChainAddForm.create(uniqueName)

        addService.addGroceryChain(input)

        val data = updateService.getGroceryChains(Instant.EPOCH, Constants.INFINITY)
        Assertions.assertThat(data).filteredOn(GroceryChainForSynchronisation::name, input.name())
            .isNotEmpty
            .allMatch { it.name() == input.name() }
    }

    @Test
    fun rename() {
        val newName = uniqueName
        val id = repository.createNew(uniqueName)

        editService.edit(GroceryChainForEditing.create(VersionedId.create(id, 0), newName))

        val data = updateService.getGroceryChains(Instant.EPOCH, Constants.INFINITY)
        Assertions.assertThat(data).filteredOn(GroceryChainForSynchronisation::name, newName)
            .isNotEmpty
            .allMatch { it.name() == newName }
    }

    @Test
    fun renamingFailsWithWrongVersion() {
        val newName = uniqueName
        val id = repository.createNew(uniqueName)

        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                editService.edit(
                    GroceryChainForEditing.create(VersionedId.create(id.id(), 99), newName),
                )
            }
            .matches { it.statusCode == StatusCode.INVALID_DATA_VERSION }
    }

    @Test
    fun renamingUnknownIdIsReported() {
        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                editService.edit(
                    GroceryChainForEditing.create(VersionedId.create(9999, 0), uniqueName),
                )
            }
            .matches { it.statusCode == StatusCode.NOT_FOUND }
    }

    @Test
    fun delete() {
        val name = uniqueName
        val id = repository.createNew(name)

        deleteService.delete(GroceryChainForDeletion.create(id.id(), 0))

        val locations = updateService.getGroceryChains(Instant.EPOCH, Constants.INFINITY)
        Assertions.assertThat(locations).filteredOn(GroceryChainForSynchronisation::name, name)
            .isNotEmpty
            .anyMatch { it.transactionTimeEnd().isBefore(Constants.INFINITY) }
    }

    @Test
    fun deletingFailsWithWrongVersion() {
        val id = repository.createNew(uniqueName)

        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy { deleteService.delete(GroceryChainForDeletion.create(id.id(), 99)) }
            .matches { it.statusCode == StatusCode.INVALID_DATA_VERSION }
    }

    @Test
    fun deletingUnknownIdIsReported() {
        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy { deleteService.delete(GroceryChainForDeletion.create(9999, 0)) }
            .matches { it.statusCode == StatusCode.NOT_FOUND }
    }
}
