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
package de.njsm.stocks.servertest

import dagger.BindsInstance
import dagger.Component
import de.njsm.stocks.client.crypto.CryptoModule
import de.njsm.stocks.client.network.NetworkModule
import de.njsm.stocks.servertest.v2.DeviceTest
import de.njsm.stocks.servertest.v2.EanTest
import de.njsm.stocks.servertest.v2.FoodItemTest
import de.njsm.stocks.servertest.v2.FoodTest
import de.njsm.stocks.servertest.v2.LocationTest
import de.njsm.stocks.servertest.v2.RecipeTest
import de.njsm.stocks.servertest.v2.RegistrationTest
import de.njsm.stocks.servertest.v2.ScaledUnitTest
import de.njsm.stocks.servertest.v2.UnitTest
import de.njsm.stocks.servertest.v2.UpdateChangeTest
import de.njsm.stocks.servertest.v2.UserTest
import org.junit.jupiter.api.TestInfo
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, CryptoModule::class, TestModule::class])
interface RootComponent {
    fun inject(locationTest: LocationTest)

    fun inject(foodItemTest: FoodItemTest)

    fun inject(updateChangeTest: UpdateChangeTest)

    fun inject(foodTest: FoodTest)

    fun inject(unitTest: UnitTest)

    fun inject(scaledUnitTest: ScaledUnitTest)

    fun inject(recipeTest: RecipeTest)

    fun inject(userTest: UserTest)

    fun inject(deviceTest: DeviceTest)

    fun inject(registrationTest: RegistrationTest)

    fun inject(eanTest: EanTest)

    @Component.Builder
    interface Builder {
        fun build(): RootComponent

        @BindsInstance
        fun withTestInfo(testInfo: TestInfo): Builder
    }
}
