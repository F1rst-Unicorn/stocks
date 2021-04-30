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

package de.njsm.stocks.server.v2.business.data;

import java.time.Instant;

public interface Bitemporal<T extends Entity<T>> extends Versionable<T> {

    Instant getValidTimeStart();

    Instant getValidTimeEnd();

    Instant getTransactionTimeStart();

    Instant getTransactionTimeEnd();

    int getInitiates();

    @Override
    default boolean isContainedIn(T item) {
        if (item instanceof Bitemporal) {
            Bitemporal<T> castedItem = (Bitemporal) item;
            return Versionable.super.isContainedIn(item) &&
                    getValidTimeStart().equals(castedItem.getValidTimeStart()) &&
                    getValidTimeEnd().equals(castedItem.getValidTimeEnd()) &&
                    getTransactionTimeStart().equals(castedItem.getTransactionTimeStart()) &&
                    getTransactionTimeEnd().equals(castedItem.getTransactionTimeEnd());
        } else {
            return false;
        }
    }
}