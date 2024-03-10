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

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.api.Entity;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.Versionable;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

interface CompleteEntityReferenceCheckerTest<Primary extends Entity<Primary>, Foreign extends Entity<Foreign>> {

    @Test
    default void checkingSetEqualityWorks() {
        Set<Versionable<Foreign>> foreigns = Set.of(getValidForeign());

        StatusCode result = getUnitUnderTest().areEntitiesComplete(getPrimary(), foreigns);

        assertEquals(StatusCode.SUCCESS, result);
    }

    @Test
    default void moreIngredientsThanInRecipeAreRejected() {
        Set<Versionable<Foreign>> ingredients = Set.of(getValidForeign(), getUnreferencedForeign());

        StatusCode result = getUnitUnderTest().areEntitiesComplete(getPrimary(), ingredients);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    default void differentVersionIsRejected() {
        Set<Versionable<Foreign>> ingredients = Set.of(getWrongVersionForeign());

        StatusCode result = getUnitUnderTest().areEntitiesComplete(getPrimary(), ingredients);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    default void differentIdIsRejected() {
        Set<Versionable<Foreign>> ingredients = Set.of(getWrongIdForeign());

        StatusCode result = getUnitUnderTest().areEntitiesComplete(getPrimary(), ingredients);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }


    @Test
    default void lessIngredientsThanInRecipeAreRejected() {
        StatusCode result = getUnitUnderTest().areEntitiesComplete(getPrimary(), Set.of());

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    CompleteReferenceChecker<Primary, Foreign> getUnitUnderTest();

    Versionable<Primary> getPrimary();

    Versionable<Foreign> getValidForeign();

    Versionable<Foreign> getUnreferencedForeign();

    Versionable<Foreign> getWrongVersionForeign();

    Versionable<Foreign> getWrongIdForeign();

}
