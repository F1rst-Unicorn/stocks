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
package de.njsm.stocks.servertest.v2.repo

import de.njsm.stocks.client.business.Constants
import de.njsm.stocks.client.business.UpdateService
import de.njsm.stocks.client.business.entities.RecipeForSynchronisation
import de.njsm.stocks.common.api.*
import de.njsm.stocks.servertest.TestSuite
import io.restassured.RestAssured
import io.restassured.common.mapper.TypeRef
import io.restassured.http.ContentType
import org.hamcrest.Matchers
import java.time.Instant
import java.util.stream.Collectors
import javax.inject.Inject

class RecipeRepository
    @Inject constructor(private val updateService: UpdateService) {

        fun getAll(): List<RecipeForSynchronisation> {
            return updateService.getRecipes(Instant.EPOCH)
                .filter { it.transactionTimeEnd() == Constants.INFINITY }
                .filter { it.validTimeStart().isBefore(Instant.now()) }
                .filter { it.validTimeEnd().isAfter(Instant.now()) }
                .toList()
        }
}
