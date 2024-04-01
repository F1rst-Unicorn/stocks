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

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import java.time.Instant

@Order(300)
class InitialServerTest : Base() {
    @BeforeEach
    fun setUp() {
        dagger.inject(this)
    }

    @Test
    fun foodIsEmpty() {
        assertThat(updateService.getFood(Instant.EPOCH))
            .isEmpty()
    }

    @Test
    fun locationsAreEmpty() {
        assertThat(updateService.getLocations(Instant.EPOCH))
            .isEmpty()
    }

    @Test
    fun foodItemsAreEmpty() {
        assertThat(updateService.getFoodItems(Instant.EPOCH))
            .isEmpty()
    }

    @Test
    fun eansAreEmpty() {
        assertThat(updateService.getEanNumbers(Instant.EPOCH))
            .isEmpty()
    }

    @Test
    fun initialUserAndSystemUserAreOnly() {
        assertThat(updateService.getUsers(Instant.EPOCH))
            .hasSize(2)
    }

    @Test
    fun initialDeviceAndSystemUserAreOnly() {
        assertThat(updateService.getUsers(Instant.EPOCH))
            .hasSize(2)
    }
}
