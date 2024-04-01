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

import de.njsm.stocks.client.business.entities.EntityType
import de.njsm.stocks.client.business.entities.LocationForSynchronisation
import de.njsm.stocks.servertest.v2.repo.LocationRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import java.time.Instant
import javax.inject.Inject

@Order(400)
class UpdateChangeTest : Base() {
    internal lateinit var locationRepository: LocationRepository
        @Inject set

    @BeforeEach
    fun setUp() {
        dagger.inject(this)
    }

    @Test
    fun updatesChangeOnDataChange() {
        var olderDate = locationChangeDate
        locationRepository.createNewLocationType(uniqueName)
        var youngerDate = locationChangeDate

        Assertions.assertTrue(olderDate < youngerDate, "$olderDate is not older than $youngerDate")

        olderDate = youngerDate
        locationRepository.createNewLocationType(uniqueName)
        youngerDate = locationChangeDate

        Assertions.assertTrue(olderDate < youngerDate, "$olderDate is not older than $youngerDate")
    }

    @Test
    fun gettingChangesStartingFromDateWorks() {
        locationRepository.createNewLocationType(uniqueName)
        val lastChangeDate = locationChangeDate
        locationRepository.createNewLocationType(uniqueName)

        assertThat(getDataYoungerThan(lastChangeDate)).hasSize(1)
    }

    private fun getDataYoungerThan(date: Instant): List<LocationForSynchronisation> {
        return updateService.getLocations(date)
    }

    private val locationChangeDate: Instant
        get() {
            val updates = updateService.updates
            return updates.stream()
                .filter { it.table() == EntityType.LOCATION }
                .map { it.lastUpdate() }
                .findFirst()
                .get()
        }
}
