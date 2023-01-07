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

package de.njsm.stocks.client.database;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BitemporalOperations {

    public static <E extends ServerDbEntity<E>, B extends ServerDbEntity.Builder<E, B>>
    List<E> currentUpdate(E current,
                          EntityEditor<E, B> editor,
                          Instant when) {
        E deletedCurrent = current.toBuilder()
                .transactionTimeEnd(when)
                .build();
        E terminatedCurrent = current.toBuilder()
                .validTimeEnd(when)
                .transactionTimeStart(when)
                .build();
        E temporaryToSatisfyTypeSystem = current.toBuilder()
                .validTimeStart(when)
                .transactionTimeStart(when)
                .version(current.version() + 1).build();
        E edited = editor.edit(temporaryToSatisfyTypeSystem.toBuilder()).build();

        return Arrays.asList(
                deletedCurrent,
                terminatedCurrent,
                edited
        );
    }

    public static <E extends ServerDbEntity<E>>
    List<E> sequencedDeleteOfEntireTime(E current, Instant when) {
        return Collections.singletonList(current.toBuilder().transactionTimeEnd(when).build());
    }

    public static <E extends ServerDbEntity<E>>
    List<E> currentDelete(E current, Instant when) {
        E deletedCurrent = current.toBuilder()
                .transactionTimeEnd(when)
                .build();
        E terminatedCurrent = current.toBuilder()
                .validTimeEnd(when)
                .transactionTimeStart(when)
                .build();

        return Arrays.asList(
                deletedCurrent,
                terminatedCurrent
        );
    }

    public interface EntityEditor<E extends ServerDbEntity<E>, B extends ServerDbEntity.Builder<E, B>> {
        B edit(B builder);
    }
}
