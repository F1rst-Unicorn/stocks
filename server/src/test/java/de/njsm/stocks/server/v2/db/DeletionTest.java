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
import org.jooq.TableRecord;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.njsm.stocks.server.v2.matchers.Matchers.*;
import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public interface DeletionTest<T extends TableRecord<T>, N extends Entity<N>> extends EntityDbTestCase<T, N>, SampleDataInformer {

    @Test
    default void deletingSuccessfullyWorks() {
        Versionable<N> data = getValidEntity();

        StatusCode result = getDbHandler().delete(data);

        assertEquals(StatusCode.SUCCESS, result);
        List<N> currentEntities = getCurrentData();
        assertThat(currentEntities, not(hasItem(matchesVersionableExactly(data))));
        assertThat(currentEntities.size(), equalTo(getNumberOfEntities() - 1));

        assertThat(getBitemporalData(), hasItem(allOf(
                matchesVersionableExactly(data),
                isTerminatedForInfinity(),
                wasInitiatedBy(TEST_USER)
        )));
    }

    @Test
    default void deletingInvalidDataVersionIsRejected() {
        Versionable<N> data = getWrongVersionEntity();

        StatusCode result = getDbHandler().delete(data);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
        assertThat(getCurrentData().size(), equalTo(getNumberOfEntities()));
    }

    @Test
    default void deletingUnknownEntityIsRejected() {
        Versionable<N> data = getUnknownEntity();

        StatusCode result = getDbHandler().delete(data);

        assertEquals(StatusCode.NOT_FOUND, result);
        assertThat(getCurrentData().size(), equalTo(getNumberOfEntities()));
    }

    Versionable<N> getUnknownEntity();

    Versionable<N> getWrongVersionEntity();

    Versionable<N> getValidEntity();
}
