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
import de.njsm.stocks.client.business.ScaledUnitAddService
import de.njsm.stocks.client.business.ScaledUnitEditService
import de.njsm.stocks.client.business.StatusCodeException
import de.njsm.stocks.client.business.entities.ScaledUnit
import de.njsm.stocks.client.business.entities.ScaledUnitAddForm
import de.njsm.stocks.client.business.entities.ScaledUnitForDeletion
import de.njsm.stocks.client.business.entities.ScaledUnitForEditing
import de.njsm.stocks.client.business.entities.ScaledUnitForSynchronisation
import de.njsm.stocks.client.business.entities.StatusCode
import de.njsm.stocks.servertest.v2.repo.UnitRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import javax.inject.Inject

@Order(1000)
class ScaledUnitTest : Base() {
    internal lateinit var unitRepository: UnitRepository
        @Inject set

    internal lateinit var scaledUnitAddService: ScaledUnitAddService
        @Inject set

    internal lateinit var scaledUnitEditService: ScaledUnitEditService
        @Inject set

    internal lateinit var scaledUnitDeleteService: EntityDeleteService<ScaledUnit>
        @Inject set

    @BeforeEach
    fun setUp() {
        dagger.inject(this)
    }

    @Test
    fun addAnItem() {
        val unit = unitRepository.createNew(uniqueName, uniqueName)
        val input = ScaledUnitAddForm.create(BigDecimal.ONE, unit.id())

        val id = scaledUnitAddService.add(input)

        val scaledUnits = updateService.getScaledUnits(Instant.EPOCH)
        assertThat(scaledUnits).filteredOn(ScaledUnitForSynchronisation::id, id.id())
            .isNotEmpty
            .allMatch { it.scale() == input.scale() }
            .allMatch { it.unit() == unit.id() }
    }

    @Test
    fun editAnItem() {
        val unit = unitRepository.createNew(uniqueName, uniqueName)
        val modifiedUnit = unitRepository.createNew(uniqueName, uniqueName)
        val id = scaledUnitAddService.add(ScaledUnitAddForm.create(BigDecimal.ONE, unit.id()))
        val input = ScaledUnitForEditing.create(id.id(), 0, BigDecimal.TEN, modifiedUnit.id())

        scaledUnitEditService.edit(input)

        val scaledUnits = updateService.getScaledUnits(Instant.EPOCH)
        assertThat(scaledUnits).filteredOn(ScaledUnitForSynchronisation::unit, modifiedUnit.id())
            .isNotEmpty
            .allMatch { it.scale() == input.scale() }
    }

    @Test
    fun editingFailsWithWrongVersion() {
        val unit = unitRepository.createNew(uniqueName, uniqueName)
        val modifiedUnit = unitRepository.createNew(uniqueName, uniqueName)
        val id = scaledUnitAddService.add(ScaledUnitAddForm.create(BigDecimal.ONE, unit.id()))
        val input = ScaledUnitForEditing.create(id.id(), 99, BigDecimal.TEN, modifiedUnit.id())

        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                scaledUnitEditService.edit(input)
            }
            .matches { it.statusCode == StatusCode.INVALID_DATA_VERSION }
    }

    @Test
    fun editingFailsIfNotFound() {
        val modifiedUnit = unitRepository.createNew(uniqueName, uniqueName)
        val input = ScaledUnitForEditing.create(9999, 0, BigDecimal.TEN, modifiedUnit.id())

        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                scaledUnitEditService.edit(input)
            }
            .matches { it.statusCode == StatusCode.NOT_FOUND }
    }

    @Test
    fun delete() {
        val unit = unitRepository.createNew(uniqueName, uniqueName)
        val id = scaledUnitAddService.add(ScaledUnitAddForm.create(BigDecimal.ONE, unit.id()))

        scaledUnitDeleteService.delete(ScaledUnitForDeletion.create(id.id(), 0))

        val scaledUnits = updateService.getScaledUnits(Instant.EPOCH)
        assertThat(scaledUnits).filteredOn(ScaledUnitForSynchronisation::unit, unit.id())
            .isNotEmpty
            .anyMatch { it.transactionTimeEnd().isBefore(Constants.INFINITY) }
    }

    @Test
    fun deletingFailsWithWrongVersion() {
        val unit = unitRepository.createNew(uniqueName, uniqueName)
        val id = scaledUnitAddService.add(ScaledUnitAddForm.create(BigDecimal.ONE, unit.id()))

        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                scaledUnitDeleteService.delete(ScaledUnitForDeletion.create(id.id(), 99))
            }
            .matches { it.statusCode == StatusCode.INVALID_DATA_VERSION }
    }

    @Test
    fun deletingUnknownIdIsReported() {
        val input = ScaledUnitForDeletion.create(9999, 0)

        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                scaledUnitDeleteService.delete(input)
            }
            .matches { it.statusCode == StatusCode.NOT_FOUND }
    }
}
