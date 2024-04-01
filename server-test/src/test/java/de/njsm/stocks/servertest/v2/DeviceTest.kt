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
import de.njsm.stocks.client.business.UserDeviceAddService
import de.njsm.stocks.client.business.entities.UserDevice
import de.njsm.stocks.client.business.entities.UserDeviceAddForm
import de.njsm.stocks.client.business.entities.UserDeviceForDeletion
import de.njsm.stocks.client.business.entities.UserDeviceForSynchronisation
import de.njsm.stocks.servertest.v2.repo.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import java.time.Instant.EPOCH
import javax.inject.Inject

@Order(1400)
class DeviceTest : Base() {
    internal lateinit var userRepository: UserRepository
        @Inject set

    internal lateinit var userDeviceAddService: UserDeviceAddService
        @Inject set

    internal lateinit var userDeviceDeleteService: EntityDeleteService<UserDevice>
        @Inject set

    @BeforeEach
    fun setUp() {
        dagger.inject(this)
    }

    @Test
    fun addDevice() {
        val name = uniqueName
        val userId = userRepository.createNewUser(uniqueName)

        val newClientTicket = userDeviceAddService.add(UserDeviceAddForm.create(name, userId))

        val devices = updateService.getUserDevices(EPOCH)
        assertThat(devices).filteredOn(UserDeviceForSynchronisation::id, newClientTicket.id().id())
            .isNotEmpty
            .allMatch { it.name() == name }
            .allMatch { it.belongsTo() == userId.id() }
    }

    @Test
    fun deleteDevice() {
        val name = uniqueName
        val userId = userRepository.createNewUser(uniqueName)
        val newUserDeviceTicket = userDeviceAddService.add(UserDeviceAddForm.create(name, userId))

        userDeviceDeleteService.delete(UserDeviceForDeletion.create(newUserDeviceTicket.id().id(), 0))

        val devices = updateService.getUserDevices(EPOCH)
        assertThat(devices).filteredOn(UserDeviceForSynchronisation::id, newUserDeviceTicket.id().id())
            .isNotEmpty
            .anyMatch { it.transactionTimeEnd().isBefore(Constants.INFINITY) }
    }
}
