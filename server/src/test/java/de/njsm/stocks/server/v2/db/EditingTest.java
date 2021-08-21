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

import de.njsm.stocks.common.api.Entity;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.Versionable;
import org.jooq.TableRecord;

import java.util.List;

import static de.njsm.stocks.server.v2.matchers.Matchers.matchesVersionableUpdated;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public interface EditingTest<T extends TableRecord<T>, N extends Entity<N>> extends EntityDbTestCase<T, N> {

    default void assertEditingWorked(Versionable<N> data, StatusCode result) {
        assertEquals(StatusCode.SUCCESS, result);
        List<N> dbData = getData();
        assertThat(dbData, hasItem(matchesVersionableUpdated(data)));
    }
}
