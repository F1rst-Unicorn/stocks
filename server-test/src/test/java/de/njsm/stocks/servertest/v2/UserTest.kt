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
import de.njsm.stocks.client.business.UserAddService
import de.njsm.stocks.client.business.entities.StatusCode
import de.njsm.stocks.client.business.entities.User
import de.njsm.stocks.client.business.entities.UserAddForm
import de.njsm.stocks.client.business.entities.UserForDeletion
import de.njsm.stocks.client.business.entities.UserForSynchronisation
import de.njsm.stocks.servertest.v2.repo.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import java.time.Instant
import javax.inject.Inject

@Order(700)
class UserTest : Base() {
    internal lateinit var userAddService: UserAddService
        @Inject set

    internal lateinit var userDeleteService: EntityDeleteService<User>
        @Inject set

    internal lateinit var userRepository: UserRepository
        @Inject set

    @BeforeEach
    fun setUp() {
        dagger.inject(this)
    }

    @Test
    fun addUser() {
        val name = uniqueName

        val id = userAddService.add(UserAddForm.create(name))

        assertThat(updateService.getUsers(Instant.EPOCH))
            .filteredOn(UserForSynchronisation::id, id.id())
            .isNotEmpty
            .allMatch { it.name() == name }
    }

    @Test
    fun addInvalidName() {
        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                userAddService.add(UserAddForm.create(""))
            }
            .matches { it.statusCode == StatusCode.INVALID_ARGUMENT }
    }

    @Test
    fun deleteInvalidVersion() {
        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                userDeleteService.delete(UserForDeletion.create(1, -1))
            }
            .matches { it.statusCode == StatusCode.INVALID_ARGUMENT }
    }

    @Test
    fun deleteInvalidId() {
        assertThatExceptionOfType(StatusCodeException::class.java)
            .isThrownBy {
                userDeleteService.delete(UserForDeletion.create(0, 1))
            }
            .matches { it.statusCode == StatusCode.INVALID_ARGUMENT }
    }

    @Test
    fun deleteUser() {
        val name = uniqueName
        val userId = userRepository.createNewUser(name)

        userDeleteService.delete(UserForDeletion.create(userId.id(), 0))

        assertThat(updateService.getUsers(Instant.EPOCH))
            .filteredOn { it.name() == name }
            .isNotEmpty
            .anyMatch { it.transactionTimeEnd().isBefore(Constants.INFINITY) }
    }
}
