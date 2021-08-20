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

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.api.Bitemporal;
import de.njsm.stocks.common.api.Entity;
import de.njsm.stocks.common.api.Insertable;
import de.njsm.stocks.common.api.StatusCode;
import fj.data.Validation;
import org.jooq.TableRecord;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.matchers.Matchers.matchesInsertable;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface EntityDbTestCase<T extends TableRecord<T>, N extends Entity<N>> extends UutGetter<T, N> {

    default void assertInsertableIsInserted(Validation<StatusCode, Integer> result,
                                            Insertable<N> data,
                                            int expectedId,
                                            int expectedNumberOfEntities) {
        assertTrue(result.isSuccess());
        assertEquals(Integer.valueOf(expectedId), result.success());

        List<N> list = getData();
        assertEquals(expectedNumberOfEntities, list.size());
        assertThat(list, hasItem(matchesInsertable(data)));
    }

    default List<N> getData() {
        Validation<StatusCode, Stream<N>> units = getDbHandler().get(false, Instant.EPOCH);
        assertTrue(units.isSuccess());
        return units.success().collect(Collectors.toList());
    }

    default List<Bitemporal<N>> getBitemporalData() {
        Validation<StatusCode, Stream<N>> units = getDbHandler().get(true, Instant.EPOCH);
        assertTrue(units.isSuccess());
        return units.success()
                .map(v -> (Bitemporal<N>) v)
                .collect(Collectors.toList());
    }
}
