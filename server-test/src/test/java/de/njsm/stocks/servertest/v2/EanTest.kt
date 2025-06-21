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
import de.njsm.stocks.client.business.EanNumberAddService
import de.njsm.stocks.client.business.EntityDeleteService
import de.njsm.stocks.client.business.StatusCodeException
import de.njsm.stocks.client.business.entities.EanNumber
import de.njsm.stocks.client.business.entities.EanNumberAddForm
import de.njsm.stocks.client.business.entities.EanNumberForDeletion
import de.njsm.stocks.client.business.entities.EanNumberForSynchronisation
import de.njsm.stocks.client.business.entities.StatusCode
import de.njsm.stocks.servertest.v2.repo.FoodRepository
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import java.time.Instant
import javax.inject.Inject

@Order(1100)
class EanTest : Base() {
    internal lateinit var foodRepository: FoodRepository
        @Inject set

    internal lateinit var eanNumberAddService: EanNumberAddService
        @Inject set

    internal lateinit var eanNumberDeleteService: EntityDeleteService<EanNumber>
        @Inject set

    @BeforeEach
    fun setUp() {
        dagger.inject(this)
    }

    @Test
    fun addAnEan() {
        val code = uniqueName
        val foodId = foodRepository.createNewFood(uniqueName)

        val id = eanNumberAddService.add(EanNumberAddForm.create(foodId, code))

        val eanNumbers = updateService.getEanNumbers(Instant.EPOCH, Constants.INFINITY)
        assertThat(eanNumbers).filteredOn(EanNumberForSynchronisation::id, id.id())
            .isNotEmpty
            .allMatch { it.identifies() == foodId.id() }
            .allMatch { it.number() == code }
    }

    @Test
    fun removeAnEan() {
        val code = uniqueName
        val foodId = foodRepository.createNewFood(uniqueName)
        val id = eanNumberAddService.add(EanNumberAddForm.create(foodId, code))

        eanNumberDeleteService.delete(EanNumberForDeletion.create(id.id(), 0))

        val eanNumbers = updateService.getEanNumbers(Instant.EPOCH, Constants.INFINITY)
        assertThat(eanNumbers).filteredOn(EanNumberForSynchronisation::number, code)
            .filteredOn { it.validTimeEnd() != Constants.INFINITY }
            .hasSize(1)
    }

    @Test
    fun removingUnknownEanIsReported() {
        Assertions.assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                eanNumberDeleteService.delete(EanNumberForDeletion.create(999, 0))
            }
            .matches { it.statusCode == StatusCode.NOT_FOUND }
    }
}
