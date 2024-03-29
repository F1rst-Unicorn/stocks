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
import de.njsm.stocks.client.business.StatusCodeException
import de.njsm.stocks.client.business.UnitAddService
import de.njsm.stocks.client.business.UnitEditService
import de.njsm.stocks.client.business.entities.StatusCode
import de.njsm.stocks.client.business.entities.Unit
import de.njsm.stocks.client.business.entities.UnitAddForm
import de.njsm.stocks.client.business.entities.UnitForDeletion
import de.njsm.stocks.client.business.entities.UnitForEditing
import de.njsm.stocks.client.business.entities.UnitForSynchronisation
import de.njsm.stocks.servertest.v2.repo.UnitRepository
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import java.time.Instant
import javax.inject.Inject

@Order(900)
class UnitTest : Base() {
    internal lateinit var unitAddService: UnitAddService
        @Inject set

    internal lateinit var unitEditService: UnitEditService
        @Inject set

    internal lateinit var unitDeleteService: EntityDeleteService<Unit>
        @Inject set

    internal lateinit var unitRepository: UnitRepository
        @Inject set

    @BeforeEach
    fun setUp() {
        dagger.inject(this)
    }

    @Test
    fun addAnItem() {
        val input = UnitAddForm.create(uniqueName, uniqueName)

        unitAddService.addUnit(input)

        val units = updateService.getUnits(Instant.EPOCH)
        Assertions.assertThat(units).filteredOn(UnitForSynchronisation::name, input.name())
            .isNotEmpty
            .allMatch { it.abbreviation() == input.abbreviation() }
    }

    @Test
    fun rename() {
        val newName = uniqueName
        val newAbbreviation = uniqueName
        val id = unitRepository.createNew(uniqueName, uniqueName)

        unitEditService.edit(
            UnitForEditing.builder()
                .id(id.id())
                .version(0)
                .name(newName)
                .abbreviation(newAbbreviation)
                .build(),
        )

        val units = updateService.getUnits(Instant.EPOCH)
        Assertions.assertThat(units).filteredOn(UnitForSynchronisation::name, newName)
            .isNotEmpty
            .allMatch { it.abbreviation() == newAbbreviation }
    }

    @Test
    fun renamingFailsWithWrongVersion() {
        val newName = uniqueName
        val newAbbreviation = uniqueName
        val id = unitRepository.createNew(uniqueName, uniqueName)

        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                unitEditService.edit(
                    UnitForEditing.builder()
                        .id(id.id())
                        .version(99)
                        .name(newName)
                        .abbreviation(newAbbreviation)
                        .build(),
                )
            }
            .matches { it.statusCode == StatusCode.INVALID_DATA_VERSION }
    }

    @Test
    fun renamingUnknownIdIsReported() {
        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                unitEditService.edit(
                    UnitForEditing.builder()
                        .id(9999)
                        .version(0)
                        .name(uniqueName)
                        .abbreviation(uniqueName)
                        .build(),
                )
            }
            .matches { it.statusCode == StatusCode.NOT_FOUND }
    }

    @Test
    fun delete() {
        val name = uniqueName
        val id = unitRepository.createNew(name, uniqueName)

        unitDeleteService.delete(UnitForDeletion.create(id.id(), 0))

        val locations = updateService.getUnits(Instant.EPOCH)
        Assertions.assertThat(locations).filteredOn(UnitForSynchronisation::name, name)
            .isNotEmpty
            .anyMatch { it.transactionTimeEnd().isBefore(Constants.INFINITY) }
    }

    @Test
    fun deletingFailsWithWrongVersion() {
        val id = unitRepository.createNew(uniqueName, uniqueName)

        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy { unitDeleteService.delete(UnitForDeletion.create(id.id(), 99)) }
            .matches { it.statusCode == StatusCode.INVALID_DATA_VERSION }
    }

    @Test
    fun deletingUnknownIdIsReported() {
        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy { unitDeleteService.delete(UnitForDeletion.create(9999, 0)) }
            .matches { it.statusCode == StatusCode.NOT_FOUND }
    }
}
